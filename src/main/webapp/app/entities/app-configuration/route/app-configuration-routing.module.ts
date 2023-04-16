import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AppConfigurationComponent } from '../list/app-configuration.component';
import { AppConfigurationDetailComponent } from '../detail/app-configuration-detail.component';
import { AppConfigurationUpdateComponent } from '../update/app-configuration-update.component';
import { AppConfigurationRoutingResolveService } from './app-configuration-routing-resolve.service';

const appConfigurationRoute: Routes = [
  {
    path: '',
    component: AppConfigurationComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AppConfigurationDetailComponent,
    resolve: {
      appConfiguration: AppConfigurationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AppConfigurationUpdateComponent,
    resolve: {
      appConfiguration: AppConfigurationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AppConfigurationUpdateComponent,
    resolve: {
      appConfiguration: AppConfigurationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(appConfigurationRoute)],
  exports: [RouterModule],
})
export class AppConfigurationRoutingModule {}
