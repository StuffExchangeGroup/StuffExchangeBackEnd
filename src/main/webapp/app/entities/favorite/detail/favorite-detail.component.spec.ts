import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { FavoriteDetailComponent } from './favorite-detail.component';

describe('Favorite Management Detail Component', () => {
  let comp: FavoriteDetailComponent;
  let fixture: ComponentFixture<FavoriteDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FavoriteDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ favorite: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(FavoriteDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(FavoriteDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load favorite on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.favorite).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
