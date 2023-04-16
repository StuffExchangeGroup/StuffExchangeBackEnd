import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { NotificationTokenDetailComponent } from './notification-token-detail.component';

describe('NotificationToken Management Detail Component', () => {
  let comp: NotificationTokenDetailComponent;
  let fixture: ComponentFixture<NotificationTokenDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NotificationTokenDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ notificationToken: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(NotificationTokenDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(NotificationTokenDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load notificationToken on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.notificationToken).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
