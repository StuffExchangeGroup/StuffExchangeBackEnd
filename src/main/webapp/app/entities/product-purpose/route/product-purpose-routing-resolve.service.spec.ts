import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IProductPurpose, ProductPurpose } from '../product-purpose.model';
import { ProductPurposeService } from '../service/product-purpose.service';

import { ProductPurposeRoutingResolveService } from './product-purpose-routing-resolve.service';

describe('ProductPurpose routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: ProductPurposeRoutingResolveService;
  let service: ProductPurposeService;
  let resultProductPurpose: IProductPurpose | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(ProductPurposeRoutingResolveService);
    service = TestBed.inject(ProductPurposeService);
    resultProductPurpose = undefined;
  });

  describe('resolve', () => {
    it('should return IProductPurpose returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultProductPurpose = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultProductPurpose).toEqual({ id: 123 });
    });

    it('should return new IProductPurpose if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultProductPurpose = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultProductPurpose).toEqual(new ProductPurpose());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as ProductPurpose })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultProductPurpose = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultProductPurpose).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
