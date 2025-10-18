import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'], // fixed typo from styleUrl to styleUrls
})
export class DashboardComponent implements OnInit {
  crops: any[] = [];
  errorMessage: string = '';
  isDeleting: boolean = false;
  successMessage: string = '';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.getAllCrops();
  }

  getAllCrops() {
    const token = localStorage.getItem('token');
    console.log('Auth token used get all crops:', token);

    if (!token) {
      this.errorMessage = 'No auth token found. Please login again.';
      return;
    }

    this.http
      .get<any[]>('http://localhost:8000/ADMINSERVICE/admin/allcrops', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .subscribe({
        next: (data) => {
          this.crops = data;
        },
        error: (error) => {
          console.error('Error fetching crops:', error);
          this.errorMessage = 'Failed to load crops. Please try again later.';
        },
      });
  }

  deleteCrop(cropId: string) {
    const token = localStorage.getItem('token');

    console.log('--- DELETE Request Triggered ---');
    console.log('Crop ID to delete:', cropId);
    console.log('Token from localStorage:', token);

    if (!token) {
      this.errorMessage = 'No auth token found. Please login again.';
      console.warn('No token found.');
      return;
    }

    if (!confirm('Are you sure you want to delete this crop?')) {
      console.log('User canceled deletion.');
      return;
    }

    this.isDeleting = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.http
      .delete(`http://localhost:8000/ADMINSERVICE/admin/deleteCrop/${cropId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
        responseType: 'text' as 'json',
      })
      .subscribe({
        next: (response: any) => {
          console.log('Successfully deleted crop:', response);
          this.crops = this.crops.filter((crop) => crop.id !== cropId);
          this.isDeleting = false;
          this.successMessage = response;
        },
        error: (error) => {
          console.error('--- ERROR Deleting Crop ---');
          console.error('Status:', error.status);
          console.error('Error Object:', error);
          if (error.status === 403) {
            this.errorMessage =
              'Access denied. You do not have permission to delete this crop.';
          } else {
            this.errorMessage =
              'Failed to delete crop. Please try again later.';
          }
          this.isDeleting = false;
        },
      });
  }
}
