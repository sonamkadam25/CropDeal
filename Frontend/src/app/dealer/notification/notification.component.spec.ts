import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NotificationComponent } from './notification.component';
import { By } from '@angular/platform-browser';

fdescribe('NotificationComponent', () => {
  let component: NotificationComponent;
  let fixture: ComponentFixture<NotificationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NotificationComponent], // ✅ Standalone component
    }).compileComponents();

    fixture = TestBed.createComponent(NotificationComponent);
    component = fixture.componentInstance;
  });

  it('should create the component', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should display crop details and notified dealers if data is provided', () => {
    component.crop = {
      name: 'Wheat',
      type: 'Grain',
      quantity: 200,
      location: 'Pune',
    };
    component.notifiedDealers = [101, 102, 103];
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;

    expect(compiled.querySelector('h3')?.textContent).toContain(
      'New Crop Posted'
    );
    expect(compiled.textContent).toContain('Wheat');
    expect(compiled.textContent).toContain('Grain');
    expect(compiled.textContent).toContain('200 kg');
    expect(compiled.textContent).toContain('Pune');
    expect(compiled.textContent).toContain('Dealer ID: 101');
    expect(compiled.textContent).toContain('Dealer ID: 102');
    expect(compiled.textContent).toContain('Dealer ID: 103');
  });

  it('should not render the notification box if crop or dealers are missing', () => {
    component.crop = undefined;
    component.notifiedDealers = undefined;
    fixture.detectChanges();

    const box = fixture.debugElement.query(By.css('.notification-box'));
    expect(box).toBeFalsy();
  });
});
