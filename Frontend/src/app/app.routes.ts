import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';

export const routes: Routes = [
  {
    path: '',
    component: HomeComponent, // Show HomeComponent at root
    pathMatch: 'full',
  },
  {
    path: 'auth',
    loadComponent: () =>
      import('./auth/auth.component').then((m) => m.AuthComponent),
    children: [
      {
        path: 'register',
        loadComponent: () =>
          import('./auth/register/register.component').then(
            (m) => m.RegisterComponent
          ),
      },
      {
        path: 'login',
        loadComponent: () =>
          import('./auth/login/login.component').then((m) => m.LoginComponent),
      },
      {
        path: 'forgot-password',
        loadComponent: () =>
          import('./auth/forgot-password/forgot-password.component').then(
            (m) => m.ForgotPasswordComponent
          ),
      },
      {
        path: 'reset-password',
        loadComponent: () =>
          import('./auth/reset-password/reset-password.component').then(
            (m) => m.ResetPasswordComponent
          ),
      },
    ],
  },
  {
    path: 'farmer',
    loadComponent: () =>
      import('./farmer/farmer.component').then((m) => m.FarmerComponent),
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./farmer/dashboard/dashboard.component').then(
            (m) => m.DashboardComponent
          ),
      },
      {
        path: 'my-crops',
        loadComponent: () =>
          import('./farmer/my-crops/my-crops.component').then(
            (m) => m.MyCropsComponent
          ),
      },
      {
        path: 'post-crop',
        loadComponent: () =>
          import('./farmer/post-crop/post-crop.component').then(
            (m) => m.PostCropComponent
          ),
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./farmer/profile/profile.component').then(
            (m) => m.ProfileComponent
          ),
      },
      {
        path: 'receipts',
        loadComponent: () =>
          import('./farmer/receipts/receipts.component').then(
            (m) => m.ReceiptsComponent
          ),
      },
    ],
  },
  {
    path: 'dealer',
    loadComponent: () =>
      import('./dealer/dealer.component').then((m) => m.DealerComponent),
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./dealer/dashboard/dashboard.component').then(
            (m) => m.DashboardComponent
          ),
      },
      {
        path: 'purchases',
        loadComponent: () =>
          import('./dealer/purchases/purchases.component').then(
            (m) => m.PurchasesComponent
          ),
      },
      {
        path: 'notification',
        loadComponent: () =>
          import('./dealer/notification/notification.component').then(
            (m) => m.NotificationComponent
          ),
      },
      {
        path: 'invoices',
        loadComponent: () =>
          import('./dealer/invoices/invoices.component').then(
            (m) => m.InvoicesComponent
          ),
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./dealer/profile/profile.component').then(
            (m) => m.ProfileComponent
          ),
      },
    ],
  },
  {
    path: 'admin',
    loadComponent: () =>
      import('./admin/admin.component').then((m) => m.AdminComponent),
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./admin/dashboard/dashboard.component').then(
            (m) => m.DashboardComponent
          ),
      },
      {
        path: 'farmers',
        loadComponent: () =>
          import('./admin/farmers/farmers.component').then(
            (m) => m.FarmersComponent
          ),
      },
      {
        path: 'dealers',
        loadComponent: () =>
          import('./admin/dealers/dealers.component').then(
            (m) => m.DealersComponent
          ),
      },
    ],
  },
  // {
  //   path: 'payment',
  //   loadChildren: () => import('./payment/payment.module').then(m => m.PaymentModule),
  // },
  {
    path: '**',
    redirectTo: '',
  },
];
