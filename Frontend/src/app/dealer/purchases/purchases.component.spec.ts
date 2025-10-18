import { ComponentFixture, TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { PurchasesComponent } from './purchases.component';

fdescribe('PurchasesComponent', () => {
  let component: PurchasesComponent;
  let fixture: ComponentFixture<PurchasesComponent>;
  let httpMock: HttpTestingController;

  const mockCrop = {
    name: 'wheat',
    type: 'cereal',
    quantity: 20,
    location: 'Pune',
    farmer: {
      id: 101,
      name: 'Farmer John',
      email: 'john@farm.com',
      phone: '9999999999',
      active: true,
      bankDetails: {
        bankName: 'SBI',
        accountNumber: '1234567890',
        ifsc: 'SBIN0000001',
      },
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, PurchasesComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(PurchasesComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);

    // Mock localStorage
    spyOn(localStorage, 'getItem').and.callFake(
      (key: string): string | null => {
        switch (key) {
          case 'selectedCrop':
            return JSON.stringify(mockCrop);
          case 'email':
            return 'dealer@example.com';
          case 'token':
            return 'mock-token';
          case 'username':
            return 'Dealer Test';
          default:
            return null;
        }
      }
    );

    // Mock Razorpay as a constructor, not just a function
    (window as any).Razorpay = function (options: any) {
      // Call handler directly to simulate success
      setTimeout(() => {
        options.handler({
          razorpay_payment_id: 'pay123',
          razorpay_order_id: options.order_id,
          razorpay_signature: 'signature123',
        });
      });
      return {
        open: () => {},
      };
    };

    fixture.detectChanges();

    // Flush dealer ID request from ngOnInit
    const dealerReq = httpMock.expectOne(
      'http://localhost:8000/DEALERSERVICE/dealer/email/dealer@example.com'
    );
    dealerReq.flush(123);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should handle crop purchase and Razorpay success', (done) => {
    component.crop = mockCrop;
    component.dealerId = 123;

    component.purchaseCrop();

    const req = httpMock.expectOne(
      'http://localhost:8000/PAYMENTSERVICE/payment/create'
    );
    expect(req.request.method).toBe('POST');

    const mockOrderResponse = {
      id: 'order_abc',
      amount: 2000,
      currency: 'INR',
    };

    req.flush(mockOrderResponse);

    // Give Razorpay’s simulated handler a moment to run
    setTimeout(() => {
      expect(component.viewSuccessPage).toBeTrue();
      expect(component.orderSummary).toEqual(
        jasmine.objectContaining({
          paymentId: 'pay123',
          orderId: 'order_abc',
          signature: 'signature123',
          dealerId: 123,
          farmerId: 101,
          amount: 2000,
          productName: 'wheat',
        })
      );
      done(); // async test done
    }, 0);
  });

  afterEach(() => {
    httpMock.verify();
  });
});
