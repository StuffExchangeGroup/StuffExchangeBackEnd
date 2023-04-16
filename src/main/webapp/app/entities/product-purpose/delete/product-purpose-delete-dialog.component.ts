import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProductPurpose } from '../product-purpose.model';
import { ProductPurposeService } from '../service/product-purpose.service';

@Component({
  templateUrl: './product-purpose-delete-dialog.component.html',
})
export class ProductPurposeDeleteDialogComponent {
  productPurpose?: IProductPurpose;

  constructor(protected productPurposeService: ProductPurposeService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.productPurposeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
