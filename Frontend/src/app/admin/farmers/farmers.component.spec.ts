import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FarmersComponent } from './farmers.component';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { FormsModule } from '@angular/forms';

fdescribe('FarmersComponent', () => {
  let component: FarmersComponent;
  let fixture: ComponentFixture<FarmersComponent>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FarmersComponent, HttpClientTestingModule, FormsModule],
    }).compileComponents();

    fixture = TestBed.createComponent(FarmersComponent);
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

  it('should fetch farmers successfully', () => {
    localStorage.setItem('token', 'mock-token');

    fixture.detectChanges(); // triggers ngOnInit → getAllFarmers()

    const mockFarmers = [
      {
        id: 1,
        name: 'Farmer One',
        email: 'f1@mail.com',
        phone: '111',
        active: true,
      },
    ];

    const req = httpMock.expectOne(
      'http://localhost:8000/ADMINSERVICE/admin/farmers'
    );
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('Bearer mock-token');

    req.flush(mockFarmers);

    expect(component.farmers.length).toBe(1);
    expect(component.farmers[0].name).toBe('Farmer One');
    expect(component.errorMessage).toBe('');
  });

  it('should show error message if farmers fetch fails', () => {
    localStorage.setItem('token', 'mock-token');

    fixture.detectChanges();

    const req = httpMock.expectOne(
      'http://localhost:8000/ADMINSERVICE/admin/farmers'
    );
    req.flush({}, { status: 500, statusText: 'Server Error' });

    expect(component.errorMessage).toBe('Failed to load farmers.');
    expect(component.farmers.length).toBe(0);
  });

  it('should start and cancel editing', () => {
    const farmer = {
      id: 2,
      name: 'Jane',
      email: 'jane@mail.com',
      phone: '222',
      active: true,
    };
    component.startEdit(farmer);

    expect(component.editFarmerId).toBe(2);
    expect(component.editedFarmer.name).toBe('Jane');

    component.cancelEdit();

    expect(component.editFarmerId).toBeNull();
    expect(component.editedFarmer).toEqual({});
  });

  it('should save edited farmer and update status', () => {
    localStorage.setItem('token', 'mock-token');

    component.editedFarmer = {
      id: 1,
      name: 'Updated',
      email: 'updated@mail.com',
      phone: '9999999999',
      password: '',
      active: false,
    };

    component.saveEdit();

    const profileReq = httpMock.expectOne(
      'http://localhost:8000/ADMINSERVICE/admin/farmer/1/update'
    );
    expect(profileReq.request.method).toBe('PUT');
    profileReq.flush({}); // Profile updated

    const statusReq = httpMock.expectOne(
      'http://localhost:8000/ADMINSERVICE/admin/farmer/1/status?active=false'
    );
    expect(statusReq.request.method).toBe('PUT');
    statusReq.flush('OK'); // Status updated

    // Should call getAllFarmers again
    const getAllReq = httpMock.expectOne(
      'http://localhost:8000/ADMINSERVICE/admin/farmers'
    );
    getAllReq.flush([]);

    expect(component.editFarmerId).toBeNull();
    expect(component.editedFarmer).toEqual({});
  });
});
