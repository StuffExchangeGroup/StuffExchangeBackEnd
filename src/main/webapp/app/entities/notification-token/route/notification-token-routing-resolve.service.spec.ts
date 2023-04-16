import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { INotificationToken, NotificationToken } from '../notification-token.model';
import { NotificationTokenService } from '../service/notification-token.service';

import { NotificationTokenRoutingResolveService } from './notification-token-routing-resolve.service';

describe('NotificationToken routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: NotificationTokenRoutingResolveService;
  let service: NotificationTokenService;
  let resultNotificationToken: INotificationToken | undefined;

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
    routingResolveService = TestBed.inject(NotificationTokenRoutingResolveService);
    service = TestBed.inject(NotificationTokenService);
    resultNotificationToken = undefined;
  });

  describe('resolve', () => {
    it('should return INotificationToken returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultNotificationToken = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultNotificationToken).toEqual({ id: 123 });
    });

    it('should return new INotificationToken if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultNotificationToken = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultNotificationToken).toEqual(new NotificationToken());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as NotificationToken })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultNotificationToken = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultNotificationToken).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
