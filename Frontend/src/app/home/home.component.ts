import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit {
  allCrops: any[] = []; // store full crop response
  filteredCrops: any[] = [];
  errorMessage = '';
  lastQuery = '';

  constructor(private readonly http: HttpClient) {}

  ngOnInit() {
    this.getCropsForHome();
  }

  ngDoCheck() {
    const currentQuery = localStorage.getItem('searchCropQuery') || '';
    if (currentQuery !== this.lastQuery) {
      this.lastQuery = currentQuery;
      this.applyFilter();
    }
  }

  getCropsForHome() {
    this.http
      .get<any[]>(
        'http://localhost:8000/FARMERSERVICE/farmers/crops/allForHomePage'
      )
      .subscribe({
        next: (data) => {
          this.allCrops = data; // get full response now
          this.applyFilter();
        },
        error: () => {
          this.errorMessage = 'Unable to load crops..';
        },
      });
  }

  applyFilter() {
    const query = localStorage.getItem('searchCropQuery') || '';
    if (!query) {
      this.filteredCrops = this.allCrops.slice(0, 12); // default 12 crops
    } else {
      const matches = this.allCrops.filter((crop) =>
        crop.name.toLowerCase().includes(query)
      );
      this.filteredCrops = matches.slice(0, 12); // show top 12 matching
    }
  }
}
