import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css'],
})
export class ResetPasswordComponent {
  resetForm: FormGroup;
  email: string = localStorage.getItem('resetEmail') || '';

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router
  ) {
    this.resetForm = this.fb.group({
      otp: ['', Validators.required],
      newPassword: ['', Validators.required],
      confirmPassword: ['', Validators.required],
    });
  }

  showPassword: boolean = false;

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  resetPassword() {
    const { otp, newPassword, confirmPassword } = this.resetForm.value;

    if (newPassword !== confirmPassword) {
      alert("Passwords don't match");
      return;
    }

    this.http
      .post(
        'http://localhost:8000/FARMERSERVICE/farmers/reset-password',
        {
          email: this.email,
          otp,
          newPassword,
        },
        {
          responseType: 'text' as 'json', // ✅ fix: treat plain text as JSON
        }
      )
      .subscribe({
        next: () => {
          alert('Password reset successful!');
          localStorage.removeItem('resetEmail');
          this.router.navigate(['/auth/login']);
        },
        error: (err) => {
          console.error('Reset error:', err);
          alert(err.error?.message || err.error || 'Failed to reset password');
        },
      });
  }
}
