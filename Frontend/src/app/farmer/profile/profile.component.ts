import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormBuilder, FormGroup } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent implements OnInit {
  farmer: any = null;
  errorMessage: string = '';
  updateMode = false;
  showBankForm = false;

  profileForm!: FormGroup;
  bankForm!: FormGroup;

  constructor(private http: HttpClient, private fb: FormBuilder) {}

  ngOnInit(): void {
    const token = localStorage.getItem('token');
    const email = localStorage.getItem('email');

    if (!token || !email) {
      this.errorMessage = 'No auth token or email found. Please login again.';
      return;
    }

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    this.http
      .get<any[]>(`http://localhost:8000/FARMERSERVICE/farmers/crops/all`, {
        headers,
      })
      .subscribe({
        next: (data) => {
          const crops = data.filter((crop) => crop.farmer.email === email);
          if (crops.length > 0) {
            this.farmer = crops[0].farmer;
            this.initProfileForm();
            this.initBankForm();
          } else {
            this.errorMessage = 'No farmer profile found.';
          }
        },
        error: (err) => {
          this.errorMessage = 'Failed to fetch farmer profile.';
          console.error(err);
        },
      });
  }

  currentSection: 'view' | 'edit' | 'bank' = 'view'; // default to profile view

  initProfileForm() {
    this.profileForm = this.fb.group({
      id: [this.farmer.id],
      name: [this.farmer.name],
      email: [this.farmer.email],
      phone: [this.farmer.phone],
      active: [this.farmer.active],
    });
  }

  initBankForm() {
    this.bankForm = this.fb.group({
      accountNumber: [''],
      ifsc: [''],
      bankName: [''],
    });
  }

  enableEdit() {
    this.updateMode = true;
  }

  saveProfile() {
    const token = localStorage.getItem('token');
    const farmerId = localStorage.getItem('farmerId');

    if (!token || !farmerId) {
      this.errorMessage = 'Missing authentication details.';
      return;
    }

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    });

    const body = this.profileForm.value;

    this.http
      .put(
        `http://localhost:8000/FARMERSERVICE/farmers/${farmerId}/update`,
        body,
        { headers }
      )
      .subscribe({
        next: (updatedFarmer) => {
          this.farmer = updatedFarmer;
          this.updateMode = false;
          alert('Profile updated successfully!');
        },
        error: (err) => {
          console.error(err);
          this.errorMessage = 'Failed to update profile.';
        },
      });
  }

  toggleBankForm() {
    this.showBankForm = !this.showBankForm;
  }

  submitBankDetails() {
    const token = localStorage.getItem('token');
    const farmerId = localStorage.getItem('farmerId');

    if (!token || !farmerId) {
      this.errorMessage = 'Missing authentication details.';
      return;
    }

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    });

    const body = this.bankForm.value;

    this.http
      .post(
        `http://localhost:8000/FARMERSERVICE/farmers/${farmerId}/bank`,
        body,
        {
          headers,
          responseType: 'text' as 'json', // 👈 This is the key fix
        }
      )
      .subscribe({
        next: (response: any) => {
          alert(response); // Will show "Bank details added successfully."
          this.showBankForm = false;
          this.bankForm.reset();
        },
        error: (err) => {
          console.error(err);
          this.errorMessage = 'Failed to add bank details.';
        },
      });
  }
}
