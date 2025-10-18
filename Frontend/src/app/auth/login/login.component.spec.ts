import {
  ComponentFixture,
  TestBed,
  fakeAsync,
  tick,
} from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { ReactiveFormsModule } from '@angular/forms';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { Router } from '@angular/router';

fdescribe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let httpMock: HttpTestingController;
  let router: Router;

  const mockRouter = {
    navigate: jasmine.createSpy('navigate'),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        LoginComponent, // standalone component
        ReactiveFormsModule,
        HttpClientTestingModule,
      ],
      providers: [{ provide: Router, useValue: mockRouter }],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear(); // clean up localStorage
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should have form with email and password controls', () => {
    expect(component.loginForm.contains('email')).toBeTrue();
    expect(component.loginForm.contains('password')).toBeTrue();
  });

  it('should show toast when form is invalid on submit', () => {
    component.loginForm.setValue({ email: '', password: '' });
    component.onSubmit();
    expect(component.showToast).toBeTrue();
    expect(component.toastMessage).toBe('Please fill in all fields correctly.');
  });

  it('should make HTTP POST request on valid form submit and navigate by role', fakeAsync(() => {
    const mockResponse = {
      role: 'FARMER',
      token: 'fake-jwt-token',
      email: 'test@example.com',
    };

    spyOn(component, 'showSuccessToast').and.callThrough();

    component.loginForm.setValue({
      email: 'test@example.com',
      password: 'password123',
    });
    component.onSubmit();

    const req = httpMock.expectOne(
      'http://localhost:8000/FARMERSERVICE/farmers/login'
    );
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);

    tick(100); // simulate navigation delay

    expect(component.showSuccessToast).toHaveBeenCalledWith(
      'Login Successfully!'
    );
    expect(localStorage.getItem('token')).toBe('fake-jwt-token');
    expect(localStorage.getItem('role')).toBe('FARMER');
    expect(localStorage.getItem('email')).toBe('test@example.com');
    expect(router.navigate).toHaveBeenCalledWith(['/farmer/dashboard']);
  }));

  it('should show toast on HTTP error', () => {
    spyOn(component, 'showSuccessToast').and.callThrough();

    component.loginForm.setValue({
      email: 'wrong@example.com',
      password: 'wrongpass',
    });
    component.onSubmit();

    const req = httpMock.expectOne(
      'http://localhost:8000/FARMERSERVICE/farmers/login'
    );
    req.flush('Invalid credentials', {
      status: 401,
      statusText: 'Unauthorized',
    });

    expect(component.showSuccessToast).toHaveBeenCalledWith(
      'Invalid credentials'
    );
  });

  it('should handle unknown role and show toast', fakeAsync(() => {
    const mockResponse = {
      role: 'UNKNOWN',
      token: 'xyz',
      email: 'test@example.com',
    };

    component.loginForm.setValue({
      email: 'test@example.com',
      password: 'pass',
    });
    component.onSubmit();

    const req = httpMock.expectOne(
      'http://localhost:8000/FARMERSERVICE/farmers/login'
    );
    req.flush(mockResponse);

    tick(100);

    expect(component.toastMessage).toBe('Invalid role!');
    expect(component.showToast).toBeTrue();
  }));
});
