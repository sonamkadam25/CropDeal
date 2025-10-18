import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProfileComponent } from './profile.component';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { ReactiveFormsModule } from '@angular/forms';

fdescribe('ProfileComponent', () => {
  let component: ProfileComponent;
  let fixture: ComponentFixture<ProfileComponent>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProfileComponent, HttpClientTestingModule, ReactiveFormsModule],
    }).compileComponents();

    fixture = TestBed.createComponent(ProfileComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should show error if no token or email in localStorage', () => {
    localStorage.removeItem('token');
    localStorage.removeItem('email');

    component.ngOnInit();

    expect(component.errorMessage).toBe(
      'No auth token or email found. Please login again.'
    );
  });

  it('should fetch farmer profile and initialize forms if data is found', () => {
    localStorage.setItem('token', 'mock-token');
    localStorage.setItem('email', 'farmer@example.com');

    const mockResponse = [
      {
        farmer: {
          id: 1,
          name: 'Test Farmer',
          email: 'farmer@example.com',
          phone: '1234567890',
          active: true,
        },
      },
    ];

    component.ngOnInit();

    const req = httpMock.expectOne(
      'http://localhost:8000/FARMERSERVICE/farmers/crops/all'
    );
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('Bearer mock-token');

    req.flush(mockResponse);

    expect(component.farmer).toEqual(mockResponse[0].farmer);
    expect(component.profileForm.value.name).toBe('Test Farmer');
  });

  it('should show error if no farmer data matches email', () => {
    localStorage.setItem('token', 'mock-token');
    localStorage.setItem('email', 'nomatch@example.com');

    const mockResponse = [
      {
        farmer: {
          id: 1,
          name: 'Test Farmer',
          email: 'other@example.com',
          phone: '1234567890',
          active: true,
        },
      },
    ];

    component.ngOnInit();

    const req = httpMock.expectOne(
      'http://localhost:8000/FARMERSERVICE/farmers/crops/all'
    );
    req.flush(mockResponse);

    expect(component.errorMessage).toBe('No farmer profile found.');
  });

  it('should handle error if fetching profile fails', () => {
    localStorage.setItem('token', 'mock-token');
    localStorage.setItem('email', 'farmer@example.com');

    component.ngOnInit();

    const req = httpMock.expectOne(
      'http://localhost:8000/FARMERSERVICE/farmers/crops/all'
    );
    req.flush('Error', { status: 500, statusText: 'Server Error' });

    expect(component.errorMessage).toBe('Failed to fetch farmer profile.');
  });

  it('should update profile successfully', () => {
    localStorage.setItem('token', 'mock-token');
    localStorage.setItem('farmerId', '1');

    component.profileForm = component['fb'].group({
      id: [1],
      name: ['Updated Name'],
      email: ['farmer@example.com'],
      phone: ['9876543210'],
      active: [true],
    });

    component.saveProfile();

    const req = httpMock.expectOne(
      'http://localhost:8000/FARMERSERVICE/farmers/1/update'
    );
    expect(req.request.method).toBe('PUT');
    req.flush({
      id: 1,
      name: 'Updated Name',
      email: 'farmer@example.com',
      phone: '9876543210',
      active: true,
    });

    expect(component.farmer.name).toBe('Updated Name');
    expect(component.updateMode).toBeFalse();
  });

  it('should handle error if updating profile fails', () => {
    localStorage.setItem('token', 'mock-token');
    localStorage.setItem('farmerId', '1');

    component.profileForm = component['fb'].group({
      id: [1],
      name: ['Fail Update'],
      email: ['farmer@example.com'],
      phone: ['0000000000'],
      active: [true],
    });

    component.saveProfile();

    const req = httpMock.expectOne(
      'http://localhost:8000/FARMERSERVICE/farmers/1/update'
    );
    req.flush('Error', { status: 500, statusText: 'Internal Server Error' });

    expect(component.errorMessage).toBe('Failed to update profile.');
  });

  it('should submit bank details successfully', () => {
    localStorage.setItem('token', 'mock-token');
    localStorage.setItem('farmerId', '1');

    component.bankForm = component['fb'].group({
      accountNumber: ['123456789'],
      ifsc: ['ABCD0123456'],
      bankName: ['Test Bank'],
    });

    component.submitBankDetails();

    const req = httpMock.expectOne(
      'http://localhost:8000/FARMERSERVICE/farmers/1/bank'
    );
    expect(req.request.method).toBe('POST');
    expect(req.request.body.accountNumber).toBe('123456789');

    req.flush('Bank details added successfully.');

    expect(component.showBankForm).toBeFalse();
    expect(component.bankForm.value).toEqual({
      accountNumber: null,
      ifsc: null,
      bankName: null,
    });
  });

  it('should handle error if submitting bank details fails', () => {
    localStorage.setItem('token', 'mock-token');
    localStorage.setItem('farmerId', '1');

    component.bankForm = component['fb'].group({
      accountNumber: ['000'],
      ifsc: ['WRONG'],
      bankName: ['FailBank'],
    });

    component.submitBankDetails();

    const req = httpMock.expectOne(
      'http://localhost:8000/FARMERSERVICE/farmers/1/bank'
    );
    req.flush('Error', { status: 400, statusText: 'Bad Request' });

    expect(component.errorMessage).toBe('Failed to add bank details.');
  });
});
