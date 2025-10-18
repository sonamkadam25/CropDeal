import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PostCropComponent } from './post-crop.component';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { ReactiveFormsModule } from '@angular/forms';

fdescribe('PostCropComponent', () => {
  let component: PostCropComponent;
  let fixture: ComponentFixture<PostCropComponent>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        PostCropComponent,
        HttpClientTestingModule,
        ReactiveFormsModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PostCropComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should show validation message if form is invalid', () => {
    component.cropForm.setValue({
      type: '',
      name: '',
      quantity: null,
      location: '',
    });

    component.onSubmit();

    expect(component.message).toBe('Please fill all fields correctly.');
  });

  it('should show auth error if token or farmerId is missing', () => {
    // Form is valid
    component.cropForm.setValue({
      type: 'fruit',
      name: 'Apple',
      quantity: 100,
      location: 'Nashik',
    });

    // Don't set localStorage values
    localStorage.removeItem('token');
    localStorage.removeItem('farmerId');

    component.onSubmit();

    expect(component.message).toBe('Authorization failed. Please login again.');
  });

  it('should post crop successfully and reset form', () => {
    localStorage.setItem('token', 'mock-token');
    localStorage.setItem('farmerId', '123');

    component.cropForm.setValue({
      type: 'fruit',
      name: 'Banana',
      quantity: 200,
      location: 'Pune',
    });

    component.onSubmit();

    const req = httpMock.expectOne(
      'http://localhost:8000/FARMERSERVICE/farmers/crops/123/post'
    );
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('Authorization')).toBe('Bearer mock-token');

    const mockResponse = {
      crop: {
        name: 'Banana',
        quantity: 200,
        location: 'Pune',
      },
      notifiedDealers: ['dealer1@example.com', 'dealer2@example.com'],
    };

    req.flush(mockResponse);

    expect(component.message).toBe('Crop posted successfully!');
    expect(component.postedCrop).toEqual(mockResponse);
    expect(component.cropForm.value).toEqual({
      type: null,
      name: null,
      quantity: null,
      location: null,
    });
  });

  it('should handle API error when posting crop fails', () => {
    localStorage.setItem('token', 'mock-token');
    localStorage.setItem('farmerId', '123');

    component.cropForm.setValue({
      type: 'grain',
      name: 'Wheat',
      quantity: 300,
      location: 'Satara',
    });

    component.onSubmit();

    const req = httpMock.expectOne(
      'http://localhost:8000/FARMERSERVICE/farmers/crops/123/post'
    );
    req.flush(
      { message: 'Error occurred' },
      { status: 500, statusText: 'Internal Server Error' }
    );

    expect(component.message).toBe('Failed to post crop.');
  });
});
