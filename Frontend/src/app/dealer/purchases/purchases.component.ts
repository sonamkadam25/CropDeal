declare var Razorpay: any; // Required for Razorpay checkout.js

import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-purchases',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './purchases.component.html',
  styleUrl: './purchases.component.css',
})
export class PurchasesComponent {
  crop: any = null;
  dealerId: number | null = null;

  viewSuccessPage = false;
  orderSummary: any = null;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    const cropData = localStorage.getItem('selectedCrop');
    if (cropData) {
      this.crop = JSON.parse(cropData);
    } else {
      console.error('No crop data found in localStorage');
      return;
    }

    const email = localStorage.getItem('email');
    const token = localStorage.getItem('token');

    if (email && token) {
      const headers = { Authorization: `Bearer ${token}` };

      this.http
        .get<any>(`http://localhost:8000/DEALERSERVICE/dealer/email/${email}`, {
          headers,
        })
        .subscribe({
          next: (res) => {
            this.dealerId = res;
            console.log('✅ Dealer ID:', this.dealerId);
          },
          error: (err) => {
            console.error('❌ Error fetching dealer ID', err);
          },
        });
    } else {
      console.error('Missing email or token in localStorage');
    }
  }

  purchaseCrop() {
    console.log('✅ Purchase button clicked');

    if (!this.crop || !this.dealerId) {
      console.warn('❌ Missing crop or dealerId');
      return;
    }

    const token = localStorage.getItem('token');
    const email = localStorage.getItem('email');
    if (!token) {
      console.error('❌ No token found in localStorage');
      return;
    }

    const headers = { Authorization: `Bearer ${token}` };

    const orderPayload = {
      dealerId: this.dealerId,
      farmerId: this.crop.farmer.id,
      // amount: this.crop.quantity * 100, // Razorpay requires amount in paise (₹1 = 100 paise)
       amount: this.crop.quantity, 
      productName: this.crop.name,
      dealerEmail: email,
    };

    console.log('📦 Sending order payload:', orderPayload);

    this.http
      .post<any>(
        'http://localhost:8000/PAYMENTSERVICE/payment/create',
        orderPayload,
        { headers }
      )
      .subscribe({
        next: (res) => {
          console.log('✅ Razorpay order created:', res);

          const options = {
            key: 'rzp_test_PHkgB0mPE51dG7', // ✅ Your Razorpay test key
            amount: res.amount, // in paise
            currency: res.currency || 'INR',
            name: 'CropDeal Purchase',
            description: orderPayload.productName,
            image: 'https://your-logo.com/logo.png', // optional
            order_id: res.id, // ✅ Razorpay order ID from backend
            handler: (response: any) => {
              console.log('✅ Payment successful:', response);

              this.orderSummary = {
                paymentId: response.razorpay_payment_id,
                orderId: response.razorpay_order_id,
                signature: response.razorpay_signature,
                ...orderPayload,
                timestamp: new Date(),
              };

              this.http
                .post(
                  'http://localhost:8000/PAYMENTSERVICE/payment/confirm',
                  {
                    dealerEmail: email,
                    productName: orderPayload.productName,
                    // amount: orderPayload.amount / 100,
                     amount: orderPayload.amount ,
                    razorpayPaymentId: response.razorpay_payment_id,
                  },
                  {
                    headers: { Authorization: `Bearer ${token}` },
                  }
                )
                .subscribe({
                  next: (res) => console.log('📩 Email sent:', res),
                  error: (err) =>
                    console.error('❌ Failed to confirm payment:', err),
                });

              this.viewSuccessPage = true;
            },
            prefill: {
              name: localStorage.getItem('username') || 'Dealer',
              email: localStorage.getItem('email') || 'test@example.com',
              contact: '9999999999',
            },
            theme: {
              color: '#27ae60',
            },
          };

          const rzp = new Razorpay(options);
          rzp.open();
        },
        error: (err) => {
          console.error('❌ Payment initiation failed:', err);
        },
      });
  }
}
