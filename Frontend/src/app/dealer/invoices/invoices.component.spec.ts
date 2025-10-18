import { ComponentFixture, TestBed } from '@angular/core/testing';
import { InvoicesComponent } from './invoices.component';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';

fdescribe('InvoicesComponent', () => {
  let component: InvoicesComponent;
  let fixture: ComponentFixture<InvoicesComponent>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InvoicesComponent, HttpClientTestingModule],
    }).compileComponents();

    localStorage.setItem('token', 'mock-token');
    localStorage.setItem('email', 'dealer@example.com');

    fixture = TestBed.createComponent(InvoicesComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    fixture.detectChanges(); // triggers ngOnInit
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should create the component', () => {
    const dealerIdReq = httpMock.expectOne(
      'http://localhost:8000/DEALERSERVICE/dealer/email/dealer@example.com'
    );
    dealerIdReq.flush(10); // mock dealer ID

    const receiptsReq = httpMock.expectOne(
      'http://localhost:8000/PAYMENTSERVICE/payment/invoice/10'
    );
    receiptsReq.flush([]); // mock empty receipts

    expect(component).toBeTruthy();
  });

  it('should fetch receipts successfully', () => {
    const mockReceipts = [
      {
        id: 1,
        dealerName: 'Dealer One',
        amount: 10000,
        cropName: 'Wheat',
        date: new Date().toISOString(),
      },
    ];

    const dealerIdReq = httpMock.expectOne(
      'http://localhost:8000/DEALERSERVICE/dealer/email/dealer@example.com'
    );
    expect(dealerIdReq.request.method).toBe('GET');
    dealerIdReq.flush(10); // mock dealer ID

    const receiptsReq = httpMock.expectOne(
      'http://localhost:8000/PAYMENTSERVICE/payment/invoice/10'
    );
    expect(receiptsReq.request.method).toBe('GET');
    receiptsReq.flush(mockReceipts); // mock receipt data

    expect(component.receipts.length).toBe(1);
    expect(component.receipts[0].dealerName).toBe('Dealer One');
    expect(component.errorMessage).toBe('');
    expect(component.loading).toBeFalse();
  });

  it('should show error if dealer ID fetch fails', () => {
    const dealerIdReq = httpMock.expectOne(
      'http://localhost:8000/DEALERSERVICE/dealer/email/dealer@example.com'
    );
    dealerIdReq.error(new ErrorEvent('Network error'));

    expect(component.receipts.length).toBe(0);
    expect(component.errorMessage).toBe(
      'Could not retrieve dealer info. Please try again.'
    );
    expect(component.loading).toBeFalse();
  });

  it('should show error if receipt fetch fails after dealer ID is received', () => {
    const dealerIdReq = httpMock.expectOne(
      'http://localhost:8000/DEALERSERVICE/dealer/email/dealer@example.com'
    );
    dealerIdReq.flush(5);

    const receiptsReq = httpMock.expectOne(
      'http://localhost:8000/PAYMENTSERVICE/payment/invoice/5'
    );
    receiptsReq.error(new ErrorEvent('Server error'), {
      status: 500,
      statusText: 'Internal Server Error',
    });

    expect(component.receipts.length).toBe(0);
    expect(component.errorMessage).toBe('Failed to load receipts.');
    expect(component.loading).toBeFalse();
  });

  it('should generate and download receipt file', () => {
    // Flush dealer ID request triggered by ngOnInit
    const dealerIdReq = httpMock.expectOne(
      'http://localhost:8000/DEALERSERVICE/dealer/email/dealer@example.com'
    );
    dealerIdReq.flush(10);
    const receiptsReq = httpMock.expectOne(
      'http://localhost:8000/PAYMENTSERVICE/payment/invoice/10'
    );
    receiptsReq.flush([]); // optional, since not used in test

    const blobSpy = spyOn(window.URL, 'createObjectURL').and.returnValue(
      'blob-url'
    );
    const revokeSpy = spyOn(window.URL, 'revokeObjectURL');

    const clickSpy = jasmine.createSpy('click');
    const fakeAnchor = {
      href: '',
      download: '',
      click: clickSpy,
    } as unknown as HTMLAnchorElement;

    spyOn(document, 'createElement').and.returnValue(fakeAnchor);

    const receipt = {
      id: 99,
      dealerName: 'Test Dealer',
      amount: 15000,
      cropName: 'Corn',
      date: new Date().toISOString(),
    };

    component.downloadReceipt(receipt);

    expect(document.createElement).toHaveBeenCalledWith('a');
    expect(fakeAnchor.download).toBe('receipt-99.txt');
    expect(clickSpy).toHaveBeenCalled();
    expect(revokeSpy).toHaveBeenCalledWith('blob-url');
  });
});
