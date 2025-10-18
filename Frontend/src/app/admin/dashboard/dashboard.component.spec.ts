import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { DashboardComponent } from './dashboard.component';

fdescribe('DashboardComponent (Admin)', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardComponent, HttpClientTestingModule]
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load crops from API', () => {
    const mockToken = 'mock-admin-token';
    const mockResponse = [
      {
        id: 'crop123',
        name: 'Rice',
        type: 'grain',
        quantity: 80,
        location: 'nashik',
        farmer: {
          name: 'Suresh',
          email: 'suresh@example.com',
          phone: '9876543210'
        }
      }
    ];

    localStorage.setItem('token', mockToken);

    fixture.detectChanges();

    const req = httpMock.expectOne('http://localhost:8000/ADMINSERVICE/admin/allcrops');
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${mockToken}`);

    req.flush(mockResponse);

    expect(component.crops.length).toBe(1);
    expect(component.crops[0].name).toBe('Rice');
    expect(component.errorMessage).toBe('');
  });

  it('should show error if no token found', () => {
    localStorage.removeItem('token');
    component.getAllCrops();
    expect(component.errorMessage).toBe('No auth token found. Please login again.');
  });

  it('should delete a crop and update the crops list', fakeAsync(() => {
    spyOn(window, 'confirm').and.returnValue(true); // Simulate user confirming delete
    const mockToken = 'admin-token';
    localStorage.setItem('token', mockToken);

    // Set initial crops
    component.crops = [
      { id: '1', name: 'Wheat', type: 'grain', quantity: 100, location: 'pune', farmer: {} },
      { id: '2', name: 'Corn', type: 'grain', quantity: 200, location: 'mumbai', farmer: {} }
    ];

    component.deleteCrop('1');

    const req = httpMock.expectOne('http://localhost:8000/ADMINSERVICE/admin/deleteCrop/1');
    expect(req.request.method).toBe('DELETE');
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${mockToken}`);

    req.flush('Crop deleted successfully');

    tick(); // Handle async timing

    expect(component.crops.length).toBe(1);
    expect(component.crops[0].id).toBe('2');
    expect(component.successMessage).toBe('Crop deleted successfully');
    expect(component.errorMessage).toBe('');
    expect(component.isDeleting).toBeFalse();
  }));

  it('should handle delete error with 403', fakeAsync(() => {
    spyOn(window, 'confirm').and.returnValue(true);
    localStorage.setItem('token', 'admin-token');

    component.crops = [{ id: '1', name: 'Wheat', type: 'grain', quantity: 100, location: 'pune', farmer: {} }];
    component.deleteCrop('1');

    const req = httpMock.expectOne('http://localhost:8000/ADMINSERVICE/admin/deleteCrop/1');
    req.flush('Forbidden', { status: 403, statusText: 'Forbidden' });

    tick();

    expect(component.errorMessage).toBe('Access denied. You do not have permission to delete this crop.');
    expect(component.isDeleting).toBeFalse();
  }));

  it('should cancel deletion if user does not confirm', () => {
    spyOn(window, 'confirm').and.returnValue(false); // Simulate cancel
    const deleteSpy = spyOn(component['http'], 'delete');
    component.crops = [{ id: '1', name: 'Rice', type: 'grain', quantity: 50, location: 'goa', farmer: {} }];
    localStorage.setItem('token', 'admin-token');

    component.deleteCrop('1');

    expect(deleteSpy).not.toHaveBeenCalled();
  });
});
