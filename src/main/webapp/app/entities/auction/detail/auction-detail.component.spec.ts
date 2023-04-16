import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AuctionDetailComponent } from './auction-detail.component';

describe('Auction Management Detail Component', () => {
  let comp: AuctionDetailComponent;
  let fixture: ComponentFixture<AuctionDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AuctionDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ auction: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(AuctionDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(AuctionDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load auction on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.auction).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
