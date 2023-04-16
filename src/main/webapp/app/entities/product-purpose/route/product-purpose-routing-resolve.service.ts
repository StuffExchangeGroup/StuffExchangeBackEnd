import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProductPurpose, ProductPurpose } from '../product-purpose.model';
import { ProductPurposeService } from '../service/product-purpose.service';

@Injectable({ providedIn: 'root' })
export class ProductPurposeRoutingResolveService implements Resolve<IProductPurpose> {
  constructor(protected service: ProductPurposeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProductPurpose> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((productPurpose: HttpResponse<ProductPurpose>) => {
          if (productPurpose.body) {
            return of(productPurpose.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new ProductPurpose());
  }
}
