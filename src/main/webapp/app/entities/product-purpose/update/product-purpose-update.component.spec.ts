import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProductPurposeService } from '../service/product-purpose.service';
import { IProductPurpose, ProductPurpose } from '../product-purpose.model';

import { ProductPurposeUpdateComponent } from './product-purpose-update.component';

describe('ProductPurpose Management Update Component', () => {
  let comp: ProductPurposeUpdateComponent;
  let fixture: ComponentFixture<ProductPurposeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let productPurposeService: ProductPurposeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProductPurposeUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ProductPurposeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProductPurposeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    productPurposeService = TestBed.inject(ProductPurposeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const productPurpose: IProductPurpose = { id: 456 };

      activatedRoute.data = of({ productPurpose });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(productPurpose));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ProductPurpose>>();
      const productPurpose = { id: 123 };
      jest.spyOn(productPurposeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productPurpose });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: productPurpose }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(productPurposeService.update).toHaveBeenCalledWith(productPurpose);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ProductPurpose>>();
      const productPurpose = new ProductPurpose();
      jest.spyOn(productPurposeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productPurpose });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: productPurpose }));
      saveSubject.complete();

      // THEN
      expect(productPurposeService.create).toHaveBeenCalledWith(productPurpose);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ProductPurpose>>();
      const productPurpose = { id: 123 };
      jest.spyOn(productPurposeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productPurpose });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(productPurposeService.update).toHaveBeenCalledWith(productPurpose);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
