import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-receipts',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './receipts.component.html',
  styleUrl: './receipts.component.css',
})
export class ReceiptsComponent implements OnInit {
  receipts: any[] = [];
  loading = true;
  errorMessage = '';

  constructor(private http: HttpClient) {}

  downloadReceipt(receipt: any) {
    const content = `
Receipt ID: ${receipt.id}
Farmer Name: ${receipt.farmerName}
Amount Paid: ₹${receipt.amount / 100}
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

  ngOnInit() {
    const token = localStorage.getItem('token');
    const farmerId = localStorage.getItem('farmerId');

    if (!token || !farmerId) {
      this.errorMessage = 'Unauthorized or farmer not found!';
      this.loading = false;
      return;
    }

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    this.http
      .get<any[]>(
        `http://localhost:8000/PAYMENTSERVICE/payment/receipt/${farmerId}`,
        { headers }
      )
      .subscribe({
        next: (data) => {
          // If response is a single object, wrap it in an array
          this.receipts = Array.isArray(data) ? data : [data];
          this.loading = false;
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Failed to load receipts.';
          this.loading = false;
        },
      });
  }
}
