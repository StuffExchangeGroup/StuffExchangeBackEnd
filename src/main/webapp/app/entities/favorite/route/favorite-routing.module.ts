import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { FavoriteComponent } from '../list/favorite.component';
import { FavoriteDetailComponent } from '../detail/favorite-detail.component';
import { FavoriteUpdateComponent } from '../update/favorite-update.component';
import { FavoriteRoutingResolveService } from './favorite-routing-resolve.service';

const favoriteRoute: Routes = [
  {
    path: '',
    component: FavoriteComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: FavoriteDetailComponent,
    resolve: {
      favorite: FavoriteRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: FavoriteUpdateComponent,
    resolve: {
      favorite: FavoriteRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: FavoriteUpdateComponent,
    resolve: {
      favorite: FavoriteRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(favoriteRoute)],
  exports: [RouterModule],
})
export class FavoriteRoutingModule {}
