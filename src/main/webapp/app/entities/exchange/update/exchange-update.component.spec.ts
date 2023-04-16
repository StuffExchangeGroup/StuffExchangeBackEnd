import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ExchangeService } from '../service/exchange.service';
import { IExchange, Exchange } from '../exchange.model';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';
import { IProfile } from 'app/entities/profile/profile.model';
import { ProfileService } from 'app/entities/profile/service/profile.service';

import { ExchangeUpdateComponent } from './exchange-update.component';

describe('Exchange Management Update Component', () => {
  let comp: ExchangeUpdateComponent;
  let fixture: ComponentFixture<ExchangeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let exchangeService: ExchangeService;
  let productService: ProductService;
  let profileService: ProfileService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ExchangeUpdateComponent],
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
      .overrideTemplate(ExchangeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ExchangeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    exchangeService = TestBed.inject(ExchangeService);
    productService = TestBed.inject(ProductService);
    profileService = TestBed.inject(ProfileService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Product query and add missing value', () => {
      const exchange: IExchange = { id: 456 };
      const sendProduct: IProduct = { id: 64367 };
      exchange.sendProduct = sendProduct;
      const receiveProduct: IProduct = { id: 54782 };
      exchange.receiveProduct = receiveProduct;

      const productCollection: IProduct[] = [{ id: 22652 }];
      jest.spyOn(productService, 'query').mockReturnValue(of(new HttpResponse({ body: productCollection })));
      const additionalProducts = [sendProduct, receiveProduct];
      const expectedCollection: IProduct[] = [...additionalProducts, ...productCollection];
      jest.spyOn(productService, 'addProductToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ exchange });
      comp.ngOnInit();

      expect(productService.query).toHaveBeenCalled();
      expect(productService.addProductToCollectionIfMissing).toHaveBeenCalledWith(productCollection, ...additionalProducts);
      expect(comp.productsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Profile query and add missing value', () => {
      const exchange: IExchange = { id: 456 };
      const owner: IProfile = { id: 10840 };
      exchange.owner = owner;
      const exchanger: IProfile = { id: 44758 };
      exchange.exchanger = exchanger;

      const profileCollection: IProfile[] = [{ id: 30295 }];
      jest.spyOn(profileService, 'query').mockReturnValue(of(new HttpResponse({ body: profileCollection })));
      const additionalProfiles = [owner, exchanger];
      const expectedCollection: IProfile[] = [...additionalProfiles, ...profileCollection];
      jest.spyOn(profileService, 'addProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ exchange });
      comp.ngOnInit();

      expect(profileService.query).toHaveBeenCalled();
      expect(profileService.addProfileToCollectionIfMissing).toHaveBeenCalledWith(profileCollection, ...additionalProfiles);
      expect(comp.profilesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const exchange: IExchange = { id: 456 };
      const sendProduct: IProduct = { id: 61894 };
      exchange.sendProduct = sendProduct;
      const receiveProduct: IProduct = { id: 13813 };
      exchange.receiveProduct = receiveProduct;
      const owner: IProfile = { id: 85201 };
      exchange.owner = owner;
      const exchanger: IProfile = { id: 4311 };
      exchange.exchanger = exchanger;

      activatedRoute.data = of({ exchange });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(exchange));
      expect(comp.productsSharedCollection).toContain(sendProduct);
      expect(comp.productsSharedCollection).toContain(receiveProduct);
      expect(comp.profilesSharedCollection).toContain(owner);
      expect(comp.profilesSharedCollection).toContain(exchanger);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Exchange>>();
      const exchange = { id: 123 };
      jest.spyOn(exchangeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ exchange });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: exchange }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(exchangeService.update).toHaveBeenCalledWith(exchange);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Exchange>>();
      const exchange = new Exchange();
      jest.spyOn(exchangeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ exchange });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: exchange }));
      saveSubject.complete();

      // THEN
      expect(exchangeService.create).toHaveBeenCalledWith(exchange);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Exchange>>();
      const exchange = { id: 123 };
      jest.spyOn(exchangeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ exchange });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(exchangeService.update).toHaveBeenCalledWith(exchange);
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
