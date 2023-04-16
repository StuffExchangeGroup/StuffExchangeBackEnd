import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPurchase } from '../purchase.model';
import { PurchaseService } from '../service/purchase.service';

@Component({
  templateUrl: './purchase-delete-dialog.component.html',
})
export class PurchaseDeleteDialogComponent {
  purchase?: IPurchase;

  constructor(protected purchaseService: PurchaseService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.purchaseService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
