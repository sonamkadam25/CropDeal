import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  registerForm: FormGroup;
  message: string = '';

  toastMessage: string = '';
  showToast: boolean = false;

  selectedRole: string = 'farmer'; // default

  ngOnInit() {
    console.log('RegisterComponent loaded..');
  }

  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.registerForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      phone: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
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
    if (this.registerForm.invalid) {
      this.toastMessage = 'Please fill in all fields correctly.';
      this.showToast = true;
      return;
    }

    const requestBody = this.registerForm.value;

    let url = '';
    if (this.selectedRole === 'farmer') {
      url = 'http://localhost:8000/FARMERSERVICE/farmers/register';
    } else if (this.selectedRole === 'dealer') {
      url = 'http://localhost:8000/DEALERSERVICE/dealer/register';
    }

    this.http.post(url, requestBody).subscribe({
      next: (res) => {
        this.showSuccessToast('Registered Successfully!');
        this.registerForm.reset();
      },
      error: (err) => {
        this.showSuccessToast(err.error || 'Registration failed!');
      },
    });
  }
}
