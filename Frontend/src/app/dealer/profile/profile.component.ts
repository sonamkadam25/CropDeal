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
  dealer: any = null;
  errorMessage = '';
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

    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

    this.http
      .get<any>(`http://localhost:8000/DEALERSERVICE/dealer/email/${email}`, {
        headers,
      })
      .subscribe({
        next: (dealerId) => {
          this.http
            .get<any>(
              `http://localhost:8000/DEALERSERVICE/dealer/${dealerId}`,
              { headers }
            )
            .subscribe({
              next: (dealerRes) => {
                this.dealer = dealerRes;
                localStorage.setItem('dealerId', this.dealer.id);
                this.initProfileForm();
                this.initBankForm();
              },
              error: () =>
                (this.errorMessage = 'Failed to load dealer profile.'),
            });
        },
        error: () => (this.errorMessage = 'Failed to fetch dealer ID.'),
      });
  }

  initProfileForm() {
    this.profileForm = this.fb.group({
      id: [this.dealer.id],
      name: [this.dealer.name],
      email: [this.dealer.email],
      phone: [this.dealer.phone],
      active: [this.dealer.active],
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
    const dealerId = localStorage.getItem('dealerId');

    if (!token || !dealerId) {
      this.errorMessage = 'Missing authentication details.';
      return;
    }

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    });

    this.http
      .put(
        `http://localhost:8000/DEALERSERVICE/dealer/update/${dealerId}`,
        this.profileForm.value,
        { headers }
      )
      .subscribe({
        next: (updatedDealer) => {
          this.dealer = updatedDealer;
          this.updateMode = false;
          alert('Dealer profile updated successfully!');
        },
        error: () => (this.errorMessage = 'Failed to update dealer profile.'),
      });
  }

  toggleBankForm() {
    this.showBankForm = !this.showBankForm;
  }

  submitBankDetails() {
    const token = localStorage.getItem('token');
    const dealerId = localStorage.getItem('dealerId');

    if (!token || !dealerId) {
      this.errorMessage = 'Missing authentication details.';
      return;
    }

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    });

    this.http
      .post(
        `http://localhost:8000/DEALERSERVICE/dealer/bank/${dealerId}`,
        this.bankForm.value,
        { headers, responseType: 'text' as 'json' }
      )
      .subscribe({
        next: (response: any) => {
          alert(response);
          this.showBankForm = false;
          this.bankForm.reset();
        },
        error: () => (this.errorMessage = 'Failed to add bank details.'),
      });
  }
}
