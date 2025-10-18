import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
// export class DashboardComponent implements OnInit {

//   crops: any[] = [];
//   errorMessage: string = '';
//   constructor(private http: HttpClient) {}
//   ngOnInit() {
//     this.getAllCrops();
//   }

//   getAllCrops() {
//   const token = localStorage.getItem('token');
//   if (!token) {
//     this.errorMessage = 'No auth token found. Please login again.';
//     return;
//   }

//   this.http.get<any[]>('http://localhost:8000/FARMERSERVICE/farmers/crops/all', {
//     headers: {
//       Authorization: `Bearer ${token}`
//     }
//   })
//   .subscribe({
//     next: (data) => {
//       this.crops = data;
//     },
//     error: (error) => {
//       console.error("Error fetching crops:", error);
//       this.errorMessage = "Failed to load crops. Please try again later.";
//     }
//   });
// }
// }
export class DashboardComponent implements OnInit {
  allCrops: any[] = [];
  crops: any[] = [];
  errorMessage: string = '';
  lastQuery = '';

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.getAllCrops();
  }

  ngDoCheck() {
    const query = localStorage.getItem('searchCropQuery') || '';
    if (query !== this.lastQuery) {
      this.lastQuery = query;
      this.applyFilter(query);
    }
  }

  getAllCrops() {
    const token = localStorage.getItem('token');
    if (!token) {
      this.errorMessage = 'No auth token found. Please login again.';
      return;
    }

    this.http
      .get<any[]>('http://localhost:8000/FARMERSERVICE/farmers/crops/all', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .subscribe({
        next: (data) => {
          this.allCrops = data;
          this.applyFilter(localStorage.getItem('searchCropQuery') || '');
        },
        error: (error) => {
          console.error('Error fetching crops:', error);
          this.errorMessage = 'Failed to load crops. Please try again later.';
        },
      });
  }

  applyFilter(query: string) {
    if (!query) {
      this.crops = this.allCrops;
    } else {
      this.crops = this.allCrops.filter((crop) =>
        crop.name.toLowerCase().includes(query.toLowerCase())
      );
    }
  }
}
