import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DashboardComponent } from './dashboard.component';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';

fdescribe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardComponent, HttpClientTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;

    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load crops when API returns data', () => {
    localStorage.setItem('token', 'mock-token');

    fixture.detectChanges();

    const req = httpMock.expectOne(
      'http://localhost:8000/FARMERSERVICE/farmers/crops/all'
    );
    expect(req.request.method).toBe('GET');

    const mockResponse = [
      {
        name: 'Wheat',
        type: 'grain',
        quantity: 50,
        location: 'pune',
        farmer: {
          name: 'Ramesh',
          email: 'ramesh@example.com',
          phone: '1234567890',
        },
      },
    ];
    req.flush(mockResponse);

    expect(component.crops.length).toBe(1);
    expect(component.crops[0].name).toBe('Wheat');
    expect(component.errorMessage).toBe('');
  });

  afterEach(() => {
    httpMock.verify();
  });
});
