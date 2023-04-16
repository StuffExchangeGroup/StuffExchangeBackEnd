import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProductPurposeDetailComponent } from './product-purpose-detail.component';

describe('ProductPurpose Management Detail Component', () => {
  let comp: ProductPurposeDetailComponent;
  let fixture: ComponentFixture<ProductPurposeDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProductPurposeDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ productPurpose: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ProductPurposeDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProductPurposeDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load productPurpose on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.productPurpose).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
