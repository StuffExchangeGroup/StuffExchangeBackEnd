import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PurchaseComponent } from '../list/purchase.component';
import { PurchaseDetailComponent } from '../detail/purchase-detail.component';
import { PurchaseUpdateComponent } from '../update/purchase-update.component';
import { PurchaseRoutingResolveService } from './purchase-routing-resolve.service';

const purchaseRoute: Routes = [
  {
    path: '',
    component: PurchaseComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PurchaseDetailComponent,
    resolve: {
      purchase: PurchaseRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PurchaseUpdateComponent,
    resolve: {
      purchase: PurchaseRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PurchaseUpdateComponent,
    resolve: {
      purchase: PurchaseRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(purchaseRoute)],
  exports: [RouterModule],
})
export class PurchaseRoutingModule {}
