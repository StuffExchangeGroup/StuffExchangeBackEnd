import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProductService } from '../service/product.service';
import { IProduct, Product } from '../product.model';
import { IRating } from 'app/entities/rating/rating.model';
import { RatingService } from 'app/entities/rating/service/rating.service';
import { IProductPurpose } from 'app/entities/product-purpose/product-purpose.model';
import { ProductPurposeService } from 'app/entities/product-purpose/service/product-purpose.service';
import { ICity } from 'app/entities/city/city.model';
import { CityService } from 'app/entities/city/service/city.service';
import { IProfile } from 'app/entities/profile/profile.model';
import { ProfileService } from 'app/entities/profile/service/profile.service';

import { ProductUpdateComponent } from './product-update.component';

describe('Product Management Update Component', () => {
  let comp: ProductUpdateComponent;
  let fixture: ComponentFixture<ProductUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let productService: ProductService;
  let ratingService: RatingService;
  let productPurposeService: ProductPurposeService;
  let cityService: CityService;
  let profileService: ProfileService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProductUpdateComponent],
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
      .overrideTemplate(ProductUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProductUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    productService = TestBed.inject(ProductService);
    ratingService = TestBed.inject(RatingService);
    productPurposeService = TestBed.inject(ProductPurposeService);
    cityService = TestBed.inject(CityService);
    profileService = TestBed.inject(ProfileService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call rating query and add missing value', () => {
      const product: IProduct = { id: 456 };
      const rating: IRating = { id: 30408 };
      product.rating = rating;

      const ratingCollection: IRating[] = [{ id: 59033 }];
      jest.spyOn(ratingService, 'query').mockReturnValue(of(new HttpResponse({ body: ratingCollection })));
      const expectedCollection: IRating[] = [rating, ...ratingCollection];
      jest.spyOn(ratingService, 'addRatingToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ product });
      comp.ngOnInit();

      expect(ratingService.query).toHaveBeenCalled();
      expect(ratingService.addRatingToCollectionIfMissing).toHaveBeenCalledWith(ratingCollection, rating);
      expect(comp.ratingsCollection).toEqual(expectedCollection);
    });

    it('Should call ProductPurpose query and add missing value', () => {
      const product: IProduct = { id: 456 };
      const purposes: IProductPurpose[] = [{ id: 47885 }];
      product.purposes = purposes;

      const productPurposeCollection: IProductPurpose[] = [{ id: 34209 }];
      jest.spyOn(productPurposeService, 'query').mockReturnValue(of(new HttpResponse({ body: productPurposeCollection })));
      const additionalProductPurposes = [...purposes];
      const expectedCollection: IProductPurpose[] = [...additionalProductPurposes, ...productPurposeCollection];
      jest.spyOn(productPurposeService, 'addProductPurposeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ product });
      comp.ngOnInit();

      expect(productPurposeService.query).toHaveBeenCalled();
      expect(productPurposeService.addProductPurposeToCollectionIfMissing).toHaveBeenCalledWith(
        productPurposeCollection,
        ...additionalProductPurposes
      );
      expect(comp.productPurposesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call City query and add missing value', () => {
      const product: IProduct = { id: 456 };
      const city: ICity = { id: 92672 };
      product.city = city;

      const cityCollection: ICity[] = [{ id: 10260 }];
      jest.spyOn(cityService, 'query').mockReturnValue(of(new HttpResponse({ body: cityCollection })));
      const additionalCities = [city];
      const expectedCollection: ICity[] = [...additionalCities, ...cityCollection];
      jest.spyOn(cityService, 'addCityToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ product });
      comp.ngOnInit();

      expect(cityService.query).toHaveBeenCalled();
      expect(cityService.addCityToCollectionIfMissing).toHaveBeenCalledWith(cityCollection, ...additionalCities);
      expect(comp.citiesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Profile query and add missing value', () => {
      const product: IProduct = { id: 456 };
      const profile: IProfile = { id: 20008 };
      product.profile = profile;

      const profileCollection: IProfile[] = [{ id: 87577 }];
      jest.spyOn(profileService, 'query').mockReturnValue(of(new HttpResponse({ body: profileCollection })));
      const additionalProfiles = [profile];
      const expectedCollection: IProfile[] = [...additionalProfiles, ...profileCollection];
      jest.spyOn(profileService, 'addProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ product });
      comp.ngOnInit();

      expect(profileService.query).toHaveBeenCalled();
      expect(profileService.addProfileToCollectionIfMissing).toHaveBeenCalledWith(profileCollection, ...additionalProfiles);
      expect(comp.profilesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const product: IProduct = { id: 456 };
      const rating: IRating = { id: 39576 };
      product.rating = rating;
      const purposes: IProductPurpose = { id: 96573 };
      product.purposes = [purposes];
      const city: ICity = { id: 19733 };
      product.city = city;
      const profile: IProfile = { id: 12264 };
      product.profile = profile;

      activatedRoute.data = of({ product });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(product));
      expect(comp.ratingsCollection).toContain(rating);
      expect(comp.productPurposesSharedCollection).toContain(purposes);
      expect(comp.citiesSharedCollection).toContain(city);
      expect(comp.profilesSharedCollection).toContain(profile);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Product>>();
      const product = { id: 123 };
      jest.spyOn(productService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ product });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: product }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(productService.update).toHaveBeenCalledWith(product);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Product>>();
      const product = new Product();
      jest.spyOn(productService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ product });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: product }));
      saveSubject.complete();

      // THEN
      expect(productService.create).toHaveBeenCalledWith(product);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Product>>();
      const product = { id: 123 };
      jest.spyOn(productService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ product });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(productService.update).toHaveBeenCalledWith(product);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackRatingById', () => {
      it('Should return tracked Rating primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackRatingById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackProductPurposeById', () => {
      it('Should return tracked ProductPurpose primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackProductPurposeById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackCityById', () => {
      it('Should return tracked City primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackCityById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackProfileById', () => {
      it('Should return tracked Profile primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackProfileById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });

  describe('Getting selected relationships', () => {
    describe('getSelectedProductPurpose', () => {
      it('Should return option if no ProductPurpose is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedProductPurpose(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected ProductPurpose for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedProductPurpose(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this ProductPurpose is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedProductPurpose(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
