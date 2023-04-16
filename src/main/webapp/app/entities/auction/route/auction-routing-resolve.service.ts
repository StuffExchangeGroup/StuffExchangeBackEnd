import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAuction, Auction } from '../auction.model';
import { AuctionService } from '../service/auction.service';

@Injectable({ providedIn: 'root' })
export class AuctionRoutingResolveService implements Resolve<IAuction> {
  constructor(protected service: AuctionService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAuction> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((auction: HttpResponse<Auction>) => {
          if (auction.body) {
            return of(auction.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Auction());
  }
}
