import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AuctionComponent } from '../list/auction.component';
import { AuctionDetailComponent } from '../detail/auction-detail.component';
import { AuctionUpdateComponent } from '../update/auction-update.component';
import { AuctionRoutingResolveService } from './auction-routing-resolve.service';

const auctionRoute: Routes = [
  {
    path: '',
    component: AuctionComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AuctionDetailComponent,
    resolve: {
      auction: AuctionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AuctionUpdateComponent,
    resolve: {
      auction: AuctionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AuctionUpdateComponent,
    resolve: {
      auction: AuctionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(auctionRoute)],
  exports: [RouterModule],
})
export class AuctionRoutingModule {}
