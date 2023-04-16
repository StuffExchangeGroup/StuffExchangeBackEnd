import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProductPurposeComponent } from '../list/product-purpose.component';
import { ProductPurposeDetailComponent } from '../detail/product-purpose-detail.component';
import { ProductPurposeUpdateComponent } from '../update/product-purpose-update.component';
import { ProductPurposeRoutingResolveService } from './product-purpose-routing-resolve.service';

const productPurposeRoute: Routes = [
  {
    path: '',
    component: ProductPurposeComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProductPurposeDetailComponent,
    resolve: {
      productPurpose: ProductPurposeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProductPurposeUpdateComponent,
    resolve: {
      productPurpose: ProductPurposeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProductPurposeUpdateComponent,
    resolve: {
      productPurpose: ProductPurposeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(productPurposeRoute)],
  exports: [RouterModule],
})
export class ProductPurposeRoutingModule {}
