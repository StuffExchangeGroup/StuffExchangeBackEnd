import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProductPurposeComponent } from './list/product-purpose.component';
import { ProductPurposeDetailComponent } from './detail/product-purpose-detail.component';
import { ProductPurposeUpdateComponent } from './update/product-purpose-update.component';
import { ProductPurposeDeleteDialogComponent } from './delete/product-purpose-delete-dialog.component';
import { ProductPurposeRoutingModule } from './route/product-purpose-routing.module';

@NgModule({
  imports: [SharedModule, ProductPurposeRoutingModule],
  declarations: [
    ProductPurposeComponent,
    ProductPurposeDetailComponent,
    ProductPurposeUpdateComponent,
    ProductPurposeDeleteDialogComponent,
  ],
  entryComponents: [ProductPurposeDeleteDialogComponent],
})
export class ProductPurposeModule {}
