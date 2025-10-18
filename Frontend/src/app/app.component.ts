import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterOutlet, RouterModule } from '@angular/router';
import { NavigationEnd } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterModule, CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  title = 'crop-deal-frontend';
  isLoggedIn: boolean = false;
  role: string = '';
  searchQuery: string = '';

  constructor(private router: Router) {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.updateLoginState();
      }
    });
  }

  ngOnInit(): void {
    this.updateLoginState();
  }

  updateLoginState(): void {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('role');

    this.isLoggedIn = !!token;
    this.role = role || '';
  }

  logout(): void {
    localStorage.clear();
    this.isLoggedIn = false;
    this.role = '';
    this.router.navigate(['/']);
  }

  onSearchChange(): void {
    localStorage.setItem(
      'searchCropQuery',
      this.searchQuery.trim().toLowerCase()
    );
  }
}
