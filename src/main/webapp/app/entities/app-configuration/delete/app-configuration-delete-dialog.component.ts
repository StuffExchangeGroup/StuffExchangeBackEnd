import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IAppConfiguration } from '../app-configuration.model';
import { AppConfigurationService } from '../service/app-configuration.service';

@Component({
  templateUrl: './app-configuration-delete-dialog.component.html',
})
export class AppConfigurationDeleteDialogComponent {
  appConfiguration?: IAppConfiguration;

  constructor(protected appConfigurationService: AppConfigurationService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.appConfigurationService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
