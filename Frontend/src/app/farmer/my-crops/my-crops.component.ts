import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-my-crops',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './my-crops.component.html',
  styleUrls: ['./my-crops.component.css'],
})
export class MyCropsComponent implements OnInit {
  crops: any[] = [];
  errorMessage: string = '';

  constructor(private http: HttpClient) {}

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
          // Filter crops by logged-in user's email
          this.crops = data.filter((crop) => crop.farmer.email === email);

          if (this.crops.length > 0) {
            const farmerId = this.crops[0].farmer.id;
            localStorage.setItem('farmerId', farmerId);
          } else {
            this.errorMessage = 'No crops found for your account.';
          }
        },
        error: (err) => {
          this.errorMessage = 'Failed to fetch crops.';
          console.error(err);
        },
      });
  }
}
