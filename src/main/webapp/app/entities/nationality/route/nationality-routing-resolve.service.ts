import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { INationality, Nationality } from '../nationality.model';
import { NationalityService } from '../service/nationality.service';

@Injectable({ providedIn: 'root' })
export class NationalityRoutingResolveService implements Resolve<INationality> {
  constructor(protected service: NationalityService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<INationality> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((nationality: HttpResponse<Nationality>) => {
          if (nationality.body) {
            return of(nationality.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Nationality());
  }
}
