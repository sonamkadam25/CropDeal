import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-farmers',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './farmers.component.html',
  styleUrl: './farmers.component.css',
})
export class FarmersComponent implements OnInit {
  farmers: any[] = [];
  errorMessage: string = '';
  editFarmerId: number | null = null;
  editedFarmer: any = {};

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.getAllFarmers();
  }

  getAllFarmers(): void {
    const token = localStorage.getItem('token');
    if (!token) {
      this.errorMessage = 'No auth token found. Please login again.';
      return;
    }

    this.http
      .get<any[]>('http://localhost:8000/ADMINSERVICE/admin/farmers', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .subscribe({
        next: (data) => {
          this.farmers = data;
        },
        error: (error) => {
          console.error('Error fetching farmers:', error);
          this.errorMessage = 'Failed to load farmers.';
        },
      });
  }

  startEdit(farmer: any): void {
    this.editFarmerId = farmer.id;
    this.editedFarmer = { ...farmer };
  }

  cancelEdit(): void {
    this.editFarmerId = null;
    this.editedFarmer = {};
  }

  saveEdit(): void {
    const token = localStorage.getItem('token');
    if (!token || !this.editedFarmer.id) return;

    const { id, name, email, phone, password, active } = this.editedFarmer;

    // 1. Update profile
    const profileBody = {
      name,
      email,
      phone,
      password: password || '123', // default password if not set
    };

    const headers = {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    };

    // First, update basic profile
    this.http
      .put(
        `http://localhost:8000/ADMINSERVICE/admin/farmer/${id}/update`,
        profileBody,
        { headers }
      )
      .subscribe({
        next: () => {
          // Then, update status separately
          this.http
            .put(
              `http://localhost:8000/ADMINSERVICE/admin/farmer/${id}/status?active=${active}`,
              {},
              { headers, responseType: 'text' as 'json' } // 👈 required to prevent parse error
            )
            .subscribe({
              next: (response) => {
                console.log(response); // Optional: logs "Farmer status updated successfully."
                this.getAllFarmers(); // Refresh table
                this.cancelEdit(); // Reset edit state
              },
              error: (err) => {
                console.error('Status update failed', err);
                alert('Farmer status update failed');
              },
            });
        },
        error: (err) => {
          console.error('Profile update failed', err);
          alert('Farmer profile update failed');
        },
      });
  }
}
