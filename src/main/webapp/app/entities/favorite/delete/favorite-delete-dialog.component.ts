import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IFavorite } from '../favorite.model';
import { FavoriteService } from '../service/favorite.service';

@Component({
  templateUrl: './favorite-delete-dialog.component.html',
})
export class FavoriteDeleteDialogComponent {
  favorite?: IFavorite;

  constructor(protected favoriteService: FavoriteService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.favoriteService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
