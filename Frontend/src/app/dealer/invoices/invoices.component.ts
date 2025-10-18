import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component } from '@angular/core';

@Component({
  selector: 'app-invoices',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './invoices.component.html',
  styleUrl: './invoices.component.css',
})
export class InvoicesComponent {
  receipts: any[] = [];
  loading = true;
  errorMessage = '';

  constructor(private http: HttpClient) {}

  ngOnInit() {
    const token = localStorage.getItem('token');
    const email = localStorage.getItem('email');

    if (!token || !email) {
      this.errorMessage = 'Unauthorized. Please login again.';
      this.loading = false;
      return;
    }

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    // Step 1: Get dealer ID from email
    this.http
      .get<number>(
        `http://localhost:8000/DEALERSERVICE/dealer/email/${email}`,
        { headers }
      )
      .subscribe({
        next: (dealerId: number) => {
          // Step 2: Use dealerId to fetch receipts
          this.http
            .get<any[]>(
              `http://localhost:8000/PAYMENTSERVICE/payment/invoice/${dealerId}`,
              { headers }
            )
            .subscribe({
              next: (data) => {
                this.receipts = Array.isArray(data) ? data : [data];
                this.loading = false;
              },
              error: (err) => {
                console.error('❌ Error fetching receipts:', err);
                this.errorMessage =
                  err.error?.message || 'Failed to load receipts.';
                this.loading = false;
              },
            });
        },
        error: (err) => {
          console.error('❌ Error fetching dealer ID:', err);
          this.errorMessage =
            'Could not retrieve dealer info. Please try again.';
          this.loading = false;
        },
      });
  }

  downloadReceipt(receipt: any) {
    const content = `
Receipt ID: ${receipt.id}
Dealer Name: ${receipt.dealerName}
Amount Paid: ₹${receipt.amount}
Crop Name: ${receipt.cropName}
Date: ${new Date(receipt.date).toLocaleString()}
`;

    const blob = new Blob([content], { type: 'text/plain' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `receipt-${receipt.id}.txt`;
    a.click();
    window.URL.revokeObjectURL(url);
  }
}
