import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DealersComponent } from './dealers.component';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { FormsModule } from '@angular/forms';

fdescribe('DealersComponent', () => {
  let component: DealersComponent;
  let fixture: ComponentFixture<DealersComponent>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DealersComponent, HttpClientTestingModule, FormsModule],
    }).compileComponents();

    fixture = TestBed.createComponent(DealersComponent);
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

  it('should fetch dealers successfully', () => {
    localStorage.setItem('token', 'mock-token');

    fixture.detectChanges(); // triggers ngOnInit

    const mockDealers = [
      {
        id: 1,
        name: 'Dealer 1',
        email: 'd1@test.com',
        phone: '1234567890',
        active: true,
      },
    ];

    const req = httpMock.expectOne(
      'http://localhost:8000/ADMINSERVICE/admin/dealers'
    );
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('Bearer mock-token');

    req.flush(mockDealers);

    expect(component.dealers.length).toBe(1);
    expect(component.dealers[0].name).toBe('Dealer 1');
    expect(component.errorMessage).toBe('');
  });

  it('should set errorMessage if fetching dealers fails', () => {
    localStorage.setItem('token', 'mock-token');

    fixture.detectChanges();

    const req = httpMock.expectOne(
      'http://localhost:8000/ADMINSERVICE/admin/dealers'
    );
    req.flush({}, { status: 500, statusText: 'Internal Server Error' });

    expect(component.errorMessage).toBe('Failed to load dealers.');
    expect(component.dealers.length).toBe(0);
  });

  it('should start editing a dealer', () => {
    const mockDealer = {
      id: 2,
      name: 'John',
      email: 'john@example.com',
      phone: '9876543210',
      active: true,
    };
    component.startEdit(mockDealer);

    expect(component.editDealerId).toBe(2);
    expect(component.editedDealer).toEqual(mockDealer);
  });

  it('should cancel editing', () => {
    component.editDealerId = 3;
    component.editedDealer = { id: 3, name: 'Dummy' };

    component.cancelEdit();

    expect(component.editDealerId).toBeNull();
    expect(component.editedDealer).toEqual({});
  });

  it('should call saveEdit and update profile and status', () => {
    localStorage.setItem('token', 'mock-token');
    component.editedDealer = {
      id: 1,
      name: 'Updated',
      email: 'updated@example.com',
      phone: '0000000000',
      password: '',
      active: false,
    };

    component.saveEdit();

    const profileReq = httpMock.expectOne(
      'http://localhost:8000/admin/dealer/1/update'
    );
    expect(profileReq.request.method).toBe('PUT');
    profileReq.flush({}); // Profile updated

    const statusReq = httpMock.expectOne(
      'http://localhost:8000/ADMINSERVICE/admin/dealer/1/status?active=false'
    );
    expect(statusReq.request.method).toBe('PUT');
    statusReq.flush('Status updated');

    // It should call getAllDealers again
    const getAllReq = httpMock.expectOne(
      'http://localhost:8000/ADMINSERVICE/admin/dealers'
    );
    getAllReq.flush([]);

    expect(component.editDealerId).toBeNull();
  });
});
