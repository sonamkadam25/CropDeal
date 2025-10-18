import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, HttpClientModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  loginForm: FormGroup;
  message: string = '';

  toastMessage: string = '';
  showToast: boolean = false;

  ngOnInit() {
    console.log('LoginComponent loaded..');
  }

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
    });
  }

  showPassword: boolean = false;

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  showSuccessToast(message: string) {
    this.toastMessage = message;
    this.showToast = true;

    setTimeout(() => {
      this.showToast = false;
    }, 3000);
  }

  onSubmit() {
    if (this.loginForm.invalid) {
      this.toastMessage = 'Please fill in all fields correctly.';
      this.showToast = true;
      return;
    }

    const requestBody = this.loginForm.value;

    this.http
      .post<any>(
        'http://localhost:8000/FARMERSERVICE/farmers/login',
        requestBody
      )
      .subscribe({
        next: (res) => {
          this.showSuccessToast('Login Successfully!');
          this.loginForm.reset();

          const role = res.role;
          const token = res.token;

          localStorage.setItem('token', token);
          localStorage.setItem('role', role);

          console.log('Role is:', role);
          // On login success, store email too
          const email = res.email; // backend se email bhi aana chahiye ideally
          localStorage.setItem('email', email);

          // ✅ Wait a bit before navigation
          setTimeout(() => {
            if (role === 'FARMER') {
              this.router.navigate(['/farmer/dashboard']);
            } else if (role === 'DEALER') {
              this.router.navigate(['/dealer/dashboard']);
            } else if (role === 'ADMIN') {
              this.router.navigate(['/admin/dashboard']);
            } else {
              this.toastMessage = 'Invalid role!';
              this.showToast = true;
            }
          }, 100);

          console.log(res);
        },
        error: (err) => {
          this.showSuccessToast(err.error || 'Login failed!');
        },
      });
  }
}
