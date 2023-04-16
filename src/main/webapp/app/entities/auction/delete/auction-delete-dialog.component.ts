import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IAuction } from '../auction.model';
import { AuctionService } from '../service/auction.service';

@Component({
  templateUrl: './auction-delete-dialog.component.html',
})
export class AuctionDeleteDialogComponent {
  auction?: IAuction;

  constructor(protected auctionService: AuctionService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.auctionService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
