import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-dealer',
  standalone: true,
  imports: [RouterModule, CommonModule],
  templateUrl: './dealer.component.html',
  styleUrl: './dealer.component.css',
})
export class DealerComponent {}
