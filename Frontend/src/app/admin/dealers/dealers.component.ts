import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-dealers',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dealers.component.html',
  styleUrl: './dealers.component.css',
})
export class DealersComponent implements OnInit {
  dealers: any[] = [];
  errorMessage: string = '';
  editDealerId: number | null = null;
  editedDealer: any = {};

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.getAllDealers();
  }

  getAllDealers(): void {
    const token = localStorage.getItem('token');
    if (!token) {
      this.errorMessage = 'No auth token found. Please login again.';
      return;
    }

    this.http
      .get<any[]>('http://localhost:8000/ADMINSERVICE/admin/dealers', {
        headers: { Authorization: `Bearer ${token}` },
      })
      .subscribe({
        next: (data) => (this.dealers = data),
        error: (err) => {
          console.error('Error fetching dealers:', err);
          this.errorMessage = 'Failed to load dealers.';
        },
      });
  }

  startEdit(dealer: any): void {
    this.editDealerId = dealer.id;
    this.editedDealer = { ...dealer };
  }

  cancelEdit(): void {
    this.editDealerId = null;
    this.editedDealer = {};
  }

  saveEdit(): void {
    const token = localStorage.getItem('token');
    if (!token || !this.editedDealer.id) return;

    const { id, name, email, phone, password, active } = this.editedDealer;

    const profileBody = {
      name,
      email,
      phone,
      password: password || '123', // optional default
    };

    const headers = {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    };

    // Step 1: Update profile
    this.http
      .put(`http://localhost:8000/admin/dealer/${id}/update`, profileBody, {
        headers,
      })
      .subscribe({
        next: () => {
          // Step 2: Update status
          this.http
            .put(
              `http://localhost:8000/ADMINSERVICE/admin/dealer/${id}/status?active=${active}`,
              {},
              { headers, responseType: 'text' as 'json' }
            )
            .subscribe({
              next: () => {
                this.getAllDealers();
                this.cancelEdit();
              },
              error: (err) => {
                console.error('Dealer status update failed', err);
                alert('Dealer status update failed.');
              },
            });
        },
        error: (err) => {
          console.error('Dealer profile update failed', err);
          alert('Dealer profile update failed.');
        },
      });
  }
}
