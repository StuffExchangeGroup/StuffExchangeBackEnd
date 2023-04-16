import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { INationality } from '../nationality.model';
import { NationalityService } from '../service/nationality.service';

@Component({
  templateUrl: './nationality-delete-dialog.component.html',
})
export class NationalityDeleteDialogComponent {
  nationality?: INationality;

  constructor(protected nationalityService: NationalityService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.nationalityService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
