import { ComponentFixture, TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { DashboardComponent } from './dashboard.component';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

fdescribe('DashboardComponent (Dealer)', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let httpMock: HttpTestingController;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        DashboardComponent,
        HttpClientTestingModule,
        RouterTestingModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch crops if token is available', () => {
    localStorage.setItem('token', 'mock-token');

    // Re-initialize component
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // triggers ngOnInit

    const req = httpMock.expectOne(
      'http://localhost:8000/DEALERSERVICE/dealer/allcrops'
    );
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('Bearer mock-token');

    req.flush([
      {
        name: 'Rice',
        type: 'Grain',
        quantity: 40,
        location: 'Delhi',
        farmer: { name: 'Raj', email: 'raj@mail.com', phone: '99999' },
      },
    ]);

    expect(component.crops.length).toBe(1);
    expect(component.crops[0].name).toBe('Rice');
    expect(component.errorMessage).toBe('');
  });

  it('should set errorMessage if no token is found', () => {
    localStorage.removeItem('token');
    component.getAllCrops();
    expect(component.errorMessage).toBe(
      'No auth token found. Please login again.'
    );
  });

  it('should navigate to viewCrop with selected crop', () => {
    const spy = spyOn(router, 'navigate');
    const crop = { id: 123, name: 'Wheat' };

    component.viewCrop(crop);

    expect(localStorage.getItem('selectedCrop')).toBe(JSON.stringify(crop));
    expect(spy).toHaveBeenCalledWith(['/dealer/purchases'], {
      queryParams: { id: 123 },
    });
  });

  it('should call subscribeToCrop and succeed', () => {
    localStorage.setItem('token', 'mock-token');
    localStorage.setItem('email', 'dealer@example.com');

    const crop = { id: 101 };
    component.subscribeToCrop(crop);

    const getIdReq = httpMock.expectOne(
      'http://localhost:8000/DEALERSERVICE/dealer/email/dealer@example.com'
    );
    expect(getIdReq.request.method).toBe('GET');
    getIdReq.flush(201); // Mock dealerId as 201

    // ✅ UPDATED: dealerEmail added as query param
    const postSubReq = httpMock.expectOne(
      'http://localhost:8000/DEALERSERVICE/dealer/subscribe?dealerId=201&cropId=101&dealerEmail=dealer@example.com'
    );
    expect(postSubReq.request.method).toBe('POST');
    postSubReq.flush('Subscribed successfully');
  });

  it('should handle error if dealer ID fetch fails in subscribeToCrop', () => {
    spyOn(window, 'alert');

    // ✅ Set required localStorage items BEFORE fixture.detectChanges()
    localStorage.setItem('token', 'mock-token');
    localStorage.setItem('email', 'dealer@example.com');

    // ✅ Recreate fixture and component
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // ngOnInit runs and triggers getAllCrops()

    // ✅ Flush the GET allcrops request
    const getCropsReq = httpMock.expectOne(
      'http://localhost:8000/DEALERSERVICE/dealer/allcrops'
    );
    getCropsReq.flush([]); // Simulate successful crops fetch

    const crop = { id: 101 };
    component.subscribeToCrop(crop);

    const getIdReq = httpMock.expectOne(
      'http://localhost:8000/DEALERSERVICE/dealer/email/dealer@example.com'
    );
    getIdReq.error(new ErrorEvent('Network error')); // Simulate error

    expect(window.alert).toHaveBeenCalledWith(
      '❌ Could not get dealer information.'
    );
  });

  it('should handle subscription error after dealer ID is fetched', () => {
    spyOn(window, 'alert');
    localStorage.setItem('token', 'mock-token');
    localStorage.setItem('email', 'dealer@example.com');

    const crop = { id: 101 };
    component.subscribeToCrop(crop);

    const getIdReq = httpMock.expectOne(
      'http://localhost:8000/DEALERSERVICE/dealer/email/dealer@example.com'
    );
    getIdReq.flush(201);

    const postSubReq = httpMock.expectOne(
      'http://localhost:8000/DEALERSERVICE/dealer/subscribe?dealerId=201&cropId=101&dealerEmail=dealer@example.com'
    );
    postSubReq.error(new ErrorEvent('Subscription failed'));

    expect(window.alert).toHaveBeenCalledWith(
      '❌ Failed to subscribe. Try again later.'
    );
  });
});
