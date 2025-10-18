import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { NotificationComponent } from '../../dealer/notification/notification.component';

@Component({
  selector: 'app-post-crop',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NotificationComponent],
  templateUrl: './post-crop.component.html',
  styleUrls: ['./post-crop.component.css'],
})
export class PostCropComponent implements OnInit {
  cropForm: FormGroup;
  message: string = '';
  postedCrop: any = null;
  crops: any[] = [];
  errorMessage: string = '';

  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.cropForm = this.fb.group({
      type: ['', Validators.required],
      name: ['', Validators.required],
      quantity: [null, [Validators.required, Validators.min(1)]],
      pricePerUnit: [null, [Validators.required, Validators.min(1)]],
      location: ['', Validators.required],
    });
  }

  ngOnInit(): void {}

  onSubmit() {
    if (this.cropForm.invalid) {
      this.message = 'Please fill all fields correctly.';
      return;
    }

    const token = localStorage.getItem('token');
    const email = localStorage.getItem('email');

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
            const farmerid = this.crops[0].farmer.id;
            localStorage.setItem('farmerid', farmerid);
          } else {
            this.errorMessage = 'No crops found for your account.';
          }
        },
        error: (err) => {
          this.errorMessage = 'Failed to fetch crops.';
          console.error(err);
        },
      });

    const farmerid = localStorage.getItem('farmerid');

    if (!farmerid || !token) {
      this.message = 'Authorization failed. Please login again.';
      return;
    }

    const url = `http://localhost:8000/FARMERSERVICE/farmers/crops/${farmerid}/post`;
    const cropData = this.cropForm.value;

    this.http.post(url, cropData, { headers }).subscribe({
      next: (response) => {
        this.message = 'Crop posted successfully!';
        this.postedCrop = response;
        console.log('Response of crop posted', response); // store full response here
        this.cropForm.reset();
      },
      error: (err) => {
        this.message = 'Failed to post crop.';
        console.error(err);
      },
    });
  }
}
