import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MyCropsComponent } from './my-crops.component';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';

fdescribe('MyCropsComponent', () => {
  let component: MyCropsComponent;
  let fixture: ComponentFixture<MyCropsComponent>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyCropsComponent, HttpClientTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(MyCropsComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear(); // Clean up local storage
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch and filter crops for the logged-in user', () => {
    const mockEmail = 'test@example.com';
    const mockToken = 'mock-token';
    localStorage.setItem('email', mockEmail);
    localStorage.setItem('token', mockToken);

    fixture.detectChanges(); // triggers ngOnInit

    const req = httpMock.expectOne(
      'http://localhost:8000/FARMERSERVICE/farmers/crops/all'
    );
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe(
      `Bearer ${mockToken}`
    );

    const mockResponse = [
      {
        name: 'Tomato',
        type: 'vegetable',
        quantity: 100,
        location: 'Nashik',
        farmer: {
          name: 'Sonu',
          email: 'test@example.com',
          id: 123,
        },
      },
      {
        name: 'Corn',
        type: 'grain',
        quantity: 200,
        location: 'Pune',
        farmer: {
          name: 'Ravi',
          email: 'other@example.com',
          id: 456,
        },
      },
    ];

    req.flush(mockResponse);

    expect(component.crops.length).toBe(1);
    expect(component.crops[0].name).toBe('Tomato');
    expect(localStorage.getItem('farmerId')).toBe('123');
    expect(component.errorMessage).toBe('');
  });

  it('should show error message when no crops match logged-in user', () => {
    localStorage.setItem('email', 'nomatch@example.com');
    localStorage.setItem('token', 'mock-token');

    fixture.detectChanges();

    const req = httpMock.expectOne(
      'http://localhost:8000/FARMERSERVICE/farmers/crops/all'
    );
    req.flush([
      {
        name: 'Onion',
        type: 'vegetable',
        quantity: 50,
        location: 'Pune',
        farmer: { email: 'another@example.com', id: 789 },
      },
    ]);

    expect(component.crops.length).toBe(0);
    expect(component.errorMessage).toBe('No crops found for your account.');
  });

  it('should handle missing token or email', () => {
    localStorage.removeItem('token');
    localStorage.removeItem('email');

    fixture.detectChanges();

    expect(component.errorMessage).toBe(
      'No auth token or email found. Please login again.'
    );
  });

  it('should handle HTTP error gracefully', () => {
    localStorage.setItem('token', 'mock-token');
    localStorage.setItem('email', 'test@example.com');

    fixture.detectChanges();

    const req = httpMock.expectOne(
      'http://localhost:8000/FARMERSERVICE/farmers/crops/all'
    );
    req.flush(
      { message: 'Unauthorized' },
      { status: 401, statusText: 'Unauthorized' }
    );

    expect(component.errorMessage).toBe('Failed to fetch crops.');
    expect(component.crops.length).toBe(0);
  });
});
