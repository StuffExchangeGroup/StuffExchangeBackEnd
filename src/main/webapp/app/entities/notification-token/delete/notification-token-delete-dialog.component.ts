import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { INotificationToken } from '../notification-token.model';
import { NotificationTokenService } from '../service/notification-token.service';

@Component({
  templateUrl: './notification-token-delete-dialog.component.html',
})
export class NotificationTokenDeleteDialogComponent {
  notificationToken?: INotificationToken;

  constructor(protected notificationTokenService: NotificationTokenService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.notificationTokenService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
