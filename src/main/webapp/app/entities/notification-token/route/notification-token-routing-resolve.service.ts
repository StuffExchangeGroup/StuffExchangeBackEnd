import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { INotificationToken, NotificationToken } from '../notification-token.model';
import { NotificationTokenService } from '../service/notification-token.service';

@Injectable({ providedIn: 'root' })
export class NotificationTokenRoutingResolveService implements Resolve<INotificationToken> {
  constructor(protected service: NotificationTokenService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<INotificationToken> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((notificationToken: HttpResponse<NotificationToken>) => {
          if (notificationToken.body) {
            return of(notificationToken.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new NotificationToken());
  }
}
