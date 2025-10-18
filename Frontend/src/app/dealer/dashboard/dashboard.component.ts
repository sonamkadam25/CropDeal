import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit, DoCheck } from '@angular/core';
import { RouterModule, Router } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent implements OnInit, DoCheck {
  allCrops: any[] = [];
  crops: any[] = [];
  errorMessage: string = '';
  lastQuery: string = '';

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit() {
    this.getAllCrops();
  }

  ngDoCheck() {
    const currentQuery = localStorage.getItem('searchCropQuery') || '';
    if (currentQuery !== this.lastQuery) {
      this.lastQuery = currentQuery;
      this.applyFilter(currentQuery);
    }
  }

  getAllCrops() {
    const token = localStorage.getItem('token');
    if (!token) {
      this.errorMessage = 'No auth token found. Please login again.';
      return;
    }

    this.http
      .get<any[]>('http://localhost:8000/DEALERSERVICE/dealer/allcrops', {
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

  viewCrop(crop: any) {
    localStorage.setItem('selectedCrop', JSON.stringify(crop));
    this.router.navigate(['/dealer/purchases'], {
      queryParams: { id: crop.id },
    });
  }

  subscribeToCrop(crop: any) {
    const token = localStorage.getItem('token');
    const email = localStorage.getItem('email');

    if (!token || !email) {
      this.errorMessage = 'Missing token or email. Please login again.';
      return;
    }

    const headers = {
      Authorization: `Bearer ${token}`,
    };

    // Step 1: Get dealerId using email
    this.http
      .get<any>(`http://localhost:8000/DEALERSERVICE/dealer/email/${email}`, {
        headers,
      })
      .subscribe({
        next: (res) => {
          const dealerId = res;
          const cropId = crop.id;

          this.http
            .post(
              `http://localhost:8000/DEALERSERVICE/dealer/subscribe?dealerId=${dealerId}&cropId=${cropId}&dealerEmail=${email}`,
              {},
              { headers, responseType: 'text' as 'json' }
            )
            .subscribe({
              next: (res) => {
                console.log('✅ Subscription response:', res);
                alert('✅ Subscribed successfully to crop!');
              },
              error: (err) => {
                console.error('❌ Subscription failed', err);
                alert('❌ Failed to subscribe. Try again later.');
              },
            });
        },
        error: (err) => {
          console.error('❌ Error fetching dealer ID', err);
          alert('❌ Could not get dealer information.');
        },
      });
  }
}
