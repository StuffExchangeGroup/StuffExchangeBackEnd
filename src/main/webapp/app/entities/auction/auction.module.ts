import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { AuctionComponent } from './list/auction.component';
import { AuctionDetailComponent } from './detail/auction-detail.component';
import { AuctionUpdateComponent } from './update/auction-update.component';
import { AuctionDeleteDialogComponent } from './delete/auction-delete-dialog.component';
import { AuctionRoutingModule } from './route/auction-routing.module';

@NgModule({
  imports: [SharedModule, AuctionRoutingModule],
  declarations: [AuctionComponent, AuctionDetailComponent, AuctionUpdateComponent, AuctionDeleteDialogComponent],
  entryComponents: [AuctionDeleteDialogComponent],
})
export class AuctionModule {}
