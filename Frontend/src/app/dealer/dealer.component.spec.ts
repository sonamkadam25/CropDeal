import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DealerComponent } from './dealer.component';
import { provideRouter } from '@angular/router';

fdescribe('DealerComponent', () => {
  let component: DealerComponent;
  let fixture: ComponentFixture<DealerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DealerComponent], // since it's a standalone component
      providers: [provideRouter([])], // needed for <router-outlet>
    }).compileComponents();

    fixture = TestBed.createComponent(DealerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
