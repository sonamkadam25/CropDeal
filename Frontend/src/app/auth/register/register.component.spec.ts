import {
  ComponentFixture,
  TestBed,
  fakeAsync,
  tick,
} from '@angular/core/testing';
import { RegisterComponent } from './register.component';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';

fdescribe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RegisterComponent, // standalone component
        ReactiveFormsModule,
        FormsModule,
        HttpClientTestingModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should have form controls', () => {
    const form = component.registerForm;
    expect(form.contains('name')).toBeTrue();
    expect(form.contains('email')).toBeTrue();
    expect(form.contains('password')).toBeTrue();
    expect(form.contains('phone')).toBeTrue();
  });

  it('should show toast and not submit when form is invalid', () => {
    component.registerForm.setValue({
      name: '',
      email: '',
      password: '',
      phone: '',
    });

    component.onSubmit();

    expect(component.showToast).toBeTrue();
    expect(component.toastMessage).toBe('Please fill in all fields correctly.');
  });

  it('should call farmer register API and show success toast', fakeAsync(() => {
    component.selectedRole = 'farmer';
    component.registerForm.setValue({
      name: 'Test Farmer',
      email: 'farmer@example.com',
      password: 'testpass',
      phone: '9876543210',
    });

    spyOn(component, 'showSuccessToast').and.callThrough();
    component.onSubmit();

    const req = httpMock.expectOne(
      'http://localhost:8000/FARMERSERVICE/farmers/register'
    );
    expect(req.request.method).toBe('POST');
    req.flush({ message: 'Farmer Registered' });

    tick(3000);

    expect(component.showSuccessToast).toHaveBeenCalledWith(
      'Registered Successfully!'
    );
    expect(component.registerForm.get('name')?.value).toBeNull(); // ✅ fixed line
  }));

  it('should call dealer register API when selectedRole is dealer', () => {
    component.selectedRole = 'dealer';
    component.registerForm.setValue({
      name: 'Test Dealer',
      email: 'dealer@example.com',
      password: 'testpass',
      phone: '9876543210',
    });

    component.onSubmit();

    const req = httpMock.expectOne(
      'http://localhost:8000/DEALERSERVICE/dealer/register'
    );
    expect(req.request.method).toBe('POST');
    req.flush({ message: 'Dealer Registered' });
  });

  it('should show error toast when API fails', () => {
    spyOn(component, 'showSuccessToast').and.callThrough();

    component.selectedRole = 'farmer';
    component.registerForm.setValue({
      name: 'Error Tester',
      email: 'error@example.com',
      password: 'testpass',
      phone: '9876543210',
    });

    component.onSubmit();

    const req = httpMock.expectOne(
      'http://localhost:8000/FARMERSERVICE/farmers/register'
    );
    req.flush('Registration failed', {
      status: 500,
      statusText: 'Internal Server Error',
    });

    expect(component.showSuccessToast).toHaveBeenCalledWith(
      'Registration failed'
    );
  });
});
