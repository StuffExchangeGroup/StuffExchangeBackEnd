import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { AppConfigurationComponent } from './list/app-configuration.component';
import { AppConfigurationDetailComponent } from './detail/app-configuration-detail.component';
import { AppConfigurationUpdateComponent } from './update/app-configuration-update.component';
import { AppConfigurationDeleteDialogComponent } from './delete/app-configuration-delete-dialog.component';
import { AppConfigurationRoutingModule } from './route/app-configuration-routing.module';

@NgModule({
  imports: [SharedModule, AppConfigurationRoutingModule],
  declarations: [
    AppConfigurationComponent,
    AppConfigurationDetailComponent,
    AppConfigurationUpdateComponent,
    AppConfigurationDeleteDialogComponent,
  ],
  entryComponents: [AppConfigurationDeleteDialogComponent],
})
export class AppConfigurationModule {}
