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
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  function setupWithLocalStorage(
    email = 'dealer@example.com',
    token = 'mock-token'
  ) {
    localStorage.setItem('token', token);
    localStorage.setItem('email', email);
    fixture = TestBed.createComponent(ProfileComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    fixture.detectChanges(); // triggers ngOnInit
  }

  it('should create the component', () => {
    setupWithLocalStorage();

    const idReq = httpMock.expectOne(
      'http://localhost:8000/DEALERSERVICE/dealer/email/dealer@example.com'
    );
    idReq.flush(101);

    const profileReq = httpMock.expectOne(
      'http://localhost:8000/DEALERSERVICE/dealer/101'
    );
    profileReq.flush({
      id: 101,
      name: 'Test Dealer',
      email: 'dealer@example.com',
      phone: '9999999999',
      active: true,
    });

    expect(component).toBeTruthy();
    expect(component.dealer.name).toBe('Test Dealer');
  });

  it('should handle missing token or email', () => {
    localStorage.removeItem('token');
    localStorage.removeItem('email');

    fixture = TestBed.createComponent(ProfileComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController); // ✅ Inject it here!
    fixture.detectChanges();

    expect(component.errorMessage).toBe(
      'No auth token or email found. Please login again.'
    );
  });

  it('should handle error if dealer ID fetch fails', () => {
    setupWithLocalStorage();

    const idReq = httpMock.expectOne(
      'http://localhost:8000/DEALERSERVICE/dealer/email/dealer@example.com'
    );
    idReq.error(new ErrorEvent('Network error'));

    expect(component.errorMessage).toBe('Failed to fetch dealer ID.');
  });

  it('should handle error if dealer profile fetch fails', () => {
    setupWithLocalStorage();

    const idReq = httpMock.expectOne(
      'http://localhost:8000/DEALERSERVICE/dealer/email/dealer@example.com'
    );
    idReq.flush(123);

    const profileReq = httpMock.expectOne(
      'http://localhost:8000/DEALERSERVICE/dealer/123'
    );
    profileReq.error(new ErrorEvent('Server error'));

    expect(component.errorMessage).toBe('Failed to load dealer profile.');
  });

  it('should update dealer profile successfully', () => {
    setupWithLocalStorage();

    const dealerId = 101;

    const idReq = httpMock.expectOne(
      'http://localhost:8000/DEALERSERVICE/dealer/email/dealer@example.com'
    );
    idReq.flush(dealerId);

    const profileRes = {
      id: dealerId,
      name: 'Dealer Old',
      email: 'dealer@example.com',
      phone: '9999999999',
      active: true,
    };

    const updatedRes = {
      ...profileRes,
      name: 'Dealer Updated',
    };

    const profileReq = httpMock.expectOne(
      `http://localhost:8000/DEALERSERVICE/dealer/${dealerId}`
    );
    profileReq.flush(profileRes);

    component.enableEdit();
    component.profileForm.patchValue({ name: 'Dealer Updated' });
    component.saveProfile();

    const updateReq = httpMock.expectOne(
      `http://localhost:8000/DEALERSERVICE/dealer/update/${dealerId}`
    );
    expect(updateReq.request.method).toBe('PUT');
    updateReq.flush(updatedRes);

    expect(component.dealer.name).toBe('Dealer Updated');
    expect(component.updateMode).toBeFalse();
  });

  it('should handle error when updating dealer profile fails', () => {
    setupWithLocalStorage();
    const dealerId = 202;

    const idReq = httpMock.expectOne(
      'http://localhost:8000/DEALERSERVICE/dealer/email/dealer@example.com'
    );
    idReq.flush(dealerId);

    const profileReq = httpMock.expectOne(
      `http://localhost:8000/DEALERSERVICE/dealer/${dealerId}`
    );
    profileReq.flush({
      id: dealerId,
      name: 'Dealer',
      email: 'dealer@example.com',
      phone: '8888888888',
      active: true,
    });

    component.enableEdit();
    component.saveProfile();

    const updateReq = httpMock.expectOne(
      `http://localhost:8000/DEALERSERVICE/dealer/update/${dealerId}`
    );
    updateReq.error(new ErrorEvent('Update Failed'));

    expect(component.errorMessage).toBe('Failed to update dealer profile.');
  });

  it('should submit bank details successfully', () => {
    setupWithLocalStorage();
    const dealerId = 303;

    const idReq = httpMock.expectOne(
      'http://localhost:8000/DEALERSERVICE/dealer/email/dealer@example.com'
    );
    idReq.flush(dealerId);

    const profileReq = httpMock.expectOne(
      `http://localhost:8000/DEALERSERVICE/dealer/${dealerId}`
    );
    profileReq.flush({
      id: dealerId,
      name: 'Bank Tester',
      email: 'dealer@example.com',
      phone: '1234567890',
      active: true,
    });

    component.toggleBankForm();
    component.bankForm.patchValue({
      accountNumber: '123456789012',
      ifsc: 'TEST0001234',
      bankName: 'Test Bank',
    });

    component.submitBankDetails();

    const bankReq = httpMock.expectOne(
      `http://localhost:8000/DEALERSERVICE/dealer/bank/${dealerId}`
    );
    expect(bankReq.request.method).toBe('POST');
    bankReq.flush('Bank details added successfully.');

    expect(component.showBankForm).toBeFalse();
    expect(component.bankForm.get('accountNumber')?.value).toBeNull();
    expect(component.bankForm.get('ifsc')?.value).toBeNull();
    expect(component.bankForm.get('bankName')?.value).toBeNull();
  });

  it('should show error if bank submission fails', () => {
    setupWithLocalStorage();
    const dealerId = 404;

    const idReq = httpMock.expectOne(
      'http://localhost:8000/DEALERSERVICE/dealer/email/dealer@example.com'
    );
    idReq.flush(dealerId);

    const profileReq = httpMock.expectOne(
      `http://localhost:8000/DEALERSERVICE/dealer/${dealerId}`
    );
    profileReq.flush({
      id: dealerId,
      name: 'Bank Error',
      email: 'dealer@example.com',
      phone: '9876543210',
      active: true,
    });

    component.toggleBankForm();
    component.bankForm.patchValue({
      accountNumber: '9999999999',
      ifsc: 'ERROR9999',
      bankName: 'Error Bank',
    });

    component.submitBankDetails();

    const bankReq = httpMock.expectOne(
      `http://localhost:8000/DEALERSERVICE/dealer/bank/${dealerId}`
    );
    bankReq.error(new ErrorEvent('Bank Error'));

    expect(component.errorMessage).toBe('Failed to add bank details.');
  });
});
