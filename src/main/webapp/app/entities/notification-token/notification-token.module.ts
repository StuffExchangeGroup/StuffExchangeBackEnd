import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { NotificationTokenComponent } from './list/notification-token.component';
import { NotificationTokenDetailComponent } from './detail/notification-token-detail.component';
import { NotificationTokenUpdateComponent } from './update/notification-token-update.component';
import { NotificationTokenDeleteDialogComponent } from './delete/notification-token-delete-dialog.component';
import { NotificationTokenRoutingModule } from './route/notification-token-routing.module';

@NgModule({
  imports: [SharedModule, NotificationTokenRoutingModule],
  declarations: [
    NotificationTokenComponent,
    NotificationTokenDetailComponent,
    NotificationTokenUpdateComponent,
    NotificationTokenDeleteDialogComponent,
  ],
  entryComponents: [NotificationTokenDeleteDialogComponent],
})
export class NotificationTokenModule {}
