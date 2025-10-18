import {
  ComponentFixture,
  TestBed,
  fakeAsync,
  tick,
} from '@angular/core/testing';
import { ReceiptsComponent } from './receipts.component';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { By } from '@angular/platform-browser';

fdescribe('ReceiptsComponent', () => {
  let component: ReceiptsComponent;
  let fixture: ComponentFixture<ReceiptsComponent>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, ReceiptsComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ReceiptsComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should show error if token or farmerId is missing in localStorage', () => {
    localStorage.removeItem('token');
    localStorage.removeItem('farmerId');

    component.ngOnInit();

    expect(component.errorMessage).toBe('Unauthorized or farmer not found!');
    expect(component.loading).toBeFalse();
  });

  it('should fetch receipts if token and farmerId are available', fakeAsync(() => {
    const mockReceipts = [
      {
        id: 1,
        farmerName: 'Test Farmer',
        amount: 5000,
        cropName: 'Wheat',
        date: new Date().toISOString(),
      },
    ];

    localStorage.setItem('token', 'mockToken');
    localStorage.setItem('farmerId', '1');

    component.ngOnInit();

    const req = httpMock.expectOne(
      'http://localhost:8000/PAYMENTSERVICE/payment/receipt/1'
    );
    expect(req.request.method).toBe('GET');
    req.flush(mockReceipts);

    tick();
    expect(component.receipts.length).toBe(1);
    expect(component.loading).toBeFalse();
    expect(component.errorMessage).toBe('');
  }));

  it('should set error message on HTTP error', fakeAsync(() => {
    localStorage.setItem('token', 'mockToken');
    localStorage.setItem('farmerId', '2');

    component.ngOnInit();

    const req = httpMock.expectOne(
      'http://localhost:8000/PAYMENTSERVICE/payment/receipt/2'
    );
    req.flush(
      { message: 'Server error' },
      { status: 500, statusText: 'Internal Server Error' }
    );

    tick();
    expect(component.receipts.length).toBe(0);
    expect(component.loading).toBeFalse();
    expect(component.errorMessage).toBe('Server error');
  }));

  it('should trigger downloadReceipt method correctly', () => {
    const mockReceipt = {
      id: 123,
      farmerName: 'Test Farmer',
      amount: 1000,
      cropName: 'Tomato',
      date: new Date().toISOString(),
    };

    spyOn(document, 'createElement').and.callFake(() => {
      return {
        set href(h: string) {},
        set download(d: string) {},
        click: jasmine.createSpy('click'),
      } as any;
    });

    spyOn(window.URL, 'createObjectURL').and.returnValue('blob:url');
    spyOn(window.URL, 'revokeObjectURL');

    component.downloadReceipt(mockReceipt);

    expect(window.URL.createObjectURL).toHaveBeenCalled();
    expect(window.URL.revokeObjectURL).toHaveBeenCalled();
  });
});
