import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PurchaseDetailComponent } from './purchase-detail.component';

describe('Purchase Management Detail Component', () => {
  let comp: PurchaseDetailComponent;
  let fixture: ComponentFixture<PurchaseDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PurchaseDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ purchase: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(PurchaseDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(PurchaseDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load purchase on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.purchase).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
