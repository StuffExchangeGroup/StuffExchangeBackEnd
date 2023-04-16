import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { NationalityDetailComponent } from './nationality-detail.component';

describe('Nationality Management Detail Component', () => {
  let comp: NationalityDetailComponent;
  let fixture: ComponentFixture<NationalityDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NationalityDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ nationality: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(NationalityDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(NationalityDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load nationality on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.nationality).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
