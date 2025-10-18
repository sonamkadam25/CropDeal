import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [ReactiveFormsModule, RouterModule],
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css'],
})
export class ForgotPasswordComponent {
  forgotForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router
  ) {
    this.forgotForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
    });
  }

  sendOtp() {
    const email = this.forgotForm.value.email;

    this.http
      .post(
        'http://localhost:8000/FARMERSERVICE/farmers/send-otp',
        { email },
        { responseType: 'text' }
      )
      .subscribe({
        next: (res) => {
          console.log('✅ Response:', res);
          localStorage.setItem('resetEmail', email);
          this.router.navigate(['/auth/reset-password']);
        },
        error: (err) => alert(err.error?.text || 'Error sending OTP'),
      });
  }
}
