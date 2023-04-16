import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { NotificationTokenComponent } from '../list/notification-token.component';
import { NotificationTokenDetailComponent } from '../detail/notification-token-detail.component';
import { NotificationTokenUpdateComponent } from '../update/notification-token-update.component';
import { NotificationTokenRoutingResolveService } from './notification-token-routing-resolve.service';

const notificationTokenRoute: Routes = [
  {
    path: '',
    component: NotificationTokenComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: NotificationTokenDetailComponent,
    resolve: {
      notificationToken: NotificationTokenRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: NotificationTokenUpdateComponent,
    resolve: {
      notificationToken: NotificationTokenRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: NotificationTokenUpdateComponent,
    resolve: {
      notificationToken: NotificationTokenRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(notificationTokenRoute)],
  exports: [RouterModule],
})
export class NotificationTokenRoutingModule {}
