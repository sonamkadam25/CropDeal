import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-farmer',
  standalone: true,
  imports: [RouterModule, CommonModule],
  templateUrl: './farmer.component.html',
  styleUrl: './farmer.component.css',
})
export class FarmerComponent {}
