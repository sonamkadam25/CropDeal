import { TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import { Component } from '@angular/core';

@Component({ template: '' })
class DummyComponent {} // For router navigation stub

fdescribe('AppComponent', () => {
  let router: Router;
  let location: Location;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([
          { path: '', component: DummyComponent },
          { path: 'auth/login', component: DummyComponent },
          { path: 'dealer/dashboard', component: DummyComponent },
        ]),
        AppComponent,
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    location = TestBed.inject(Location);
    router.initialNavigation();
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it(`should have the 'crop-deal-frontend' title`, () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app.title).toEqual('crop-deal-frontend');
  });

  it('should detect user as logged in when token is present', () => {
    localStorage.setItem('token', 'mock-token');
    localStorage.setItem('role', 'DEALER');

    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    app.ngOnInit();

    expect(app.isLoggedIn).toBeTrue();
    expect(app.role).toBe('DEALER');
  });

  it('should logout and clear localStorage', async () => {
    localStorage.setItem('token', 'mock-token');
    localStorage.setItem('role', 'ADMIN');

    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;

    app.logout();

    expect(localStorage.getItem('token')).toBeNull();
    expect(app.isLoggedIn).toBeFalse();
    expect(app.role).toBe('');
  });

  it('should update login state on NavigationEnd', async () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;

    // Set values in localStorage before navigation
    localStorage.setItem('token', 'nav-token');
    localStorage.setItem('role', 'FARMER');

    // Navigate to a route to trigger NavigationEnd
    await router.navigate(['/auth/login']);
    fixture.detectChanges(); // trigger change detection

    // The router event subscription inside the constructor should run updateLoginState()
    expect(app.isLoggedIn).toBeTrue();
    expect(app.role).toBe('FARMER');
  });
});
