import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { FavoriteService } from '../service/favorite.service';
import { IFavorite, Favorite } from '../favorite.model';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';
import { IProfile } from 'app/entities/profile/profile.model';
import { ProfileService } from 'app/entities/profile/service/profile.service';

import { FavoriteUpdateComponent } from './favorite-update.component';

describe('Favorite Management Update Component', () => {
  let comp: FavoriteUpdateComponent;
  let fixture: ComponentFixture<FavoriteUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let favoriteService: FavoriteService;
  let productService: ProductService;
  let profileService: ProfileService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [FavoriteUpdateComponent],
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
      .overrideTemplate(FavoriteUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FavoriteUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    favoriteService = TestBed.inject(FavoriteService);
    productService = TestBed.inject(ProductService);
    profileService = TestBed.inject(ProfileService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Product query and add missing value', () => {
      const favorite: IFavorite = { id: 456 };
      const product: IProduct = { id: 1268 };
      favorite.product = product;

      const productCollection: IProduct[] = [{ id: 76226 }];
      jest.spyOn(productService, 'query').mockReturnValue(of(new HttpResponse({ body: productCollection })));
      const additionalProducts = [product];
      const expectedCollection: IProduct[] = [...additionalProducts, ...productCollection];
      jest.spyOn(productService, 'addProductToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ favorite });
      comp.ngOnInit();

      expect(productService.query).toHaveBeenCalled();
      expect(productService.addProductToCollectionIfMissing).toHaveBeenCalledWith(productCollection, ...additionalProducts);
      expect(comp.productsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Profile query and add missing value', () => {
      const favorite: IFavorite = { id: 456 };
      const profile: IProfile = { id: 13637 };
      favorite.profile = profile;

      const profileCollection: IProfile[] = [{ id: 80737 }];
      jest.spyOn(profileService, 'query').mockReturnValue(of(new HttpResponse({ body: profileCollection })));
      const additionalProfiles = [profile];
      const expectedCollection: IProfile[] = [...additionalProfiles, ...profileCollection];
      jest.spyOn(profileService, 'addProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ favorite });
      comp.ngOnInit();

      expect(profileService.query).toHaveBeenCalled();
      expect(profileService.addProfileToCollectionIfMissing).toHaveBeenCalledWith(profileCollection, ...additionalProfiles);
      expect(comp.profilesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const favorite: IFavorite = { id: 456 };
      const product: IProduct = { id: 90123 };
      favorite.product = product;
      const profile: IProfile = { id: 19682 };
      favorite.profile = profile;

      activatedRoute.data = of({ favorite });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(favorite));
      expect(comp.productsSharedCollection).toContain(product);
      expect(comp.profilesSharedCollection).toContain(profile);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Favorite>>();
      const favorite = { id: 123 };
      jest.spyOn(favoriteService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ favorite });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: favorite }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(favoriteService.update).toHaveBeenCalledWith(favorite);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Favorite>>();
      const favorite = new Favorite();
      jest.spyOn(favoriteService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ favorite });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: favorite }));
      saveSubject.complete();

      // THEN
      expect(favoriteService.create).toHaveBeenCalledWith(favorite);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Favorite>>();
      const favorite = { id: 123 };
      jest.spyOn(favoriteService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ favorite });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(favoriteService.update).toHaveBeenCalledWith(favorite);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackProductById', () => {
      it('Should return tracked Product primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackProductById(0, entity);
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
});
