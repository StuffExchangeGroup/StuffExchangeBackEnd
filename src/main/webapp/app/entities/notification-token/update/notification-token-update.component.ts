import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { INotificationToken, NotificationToken } from '../notification-token.model';
import { NotificationTokenService } from '../service/notification-token.service';
import { IProfile } from 'app/entities/profile/profile.model';
import { ProfileService } from 'app/entities/profile/service/profile.service';

@Component({
  selector: 'jhi-notification-token-update',
  templateUrl: './notification-token-update.component.html',
})
export class NotificationTokenUpdateComponent implements OnInit {
  isSaving = false;

  profilesSharedCollection: IProfile[] = [];

  editForm = this.fb.group({
    id: [],
    token: [],
    profile: [],
  });

  constructor(
    protected notificationTokenService: NotificationTokenService,
    protected profileService: ProfileService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ notificationToken }) => {
      this.updateForm(notificationToken);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const notificationToken = this.createFromForm();
    if (notificationToken.id !== undefined) {
      this.subscribeToSaveResponse(this.notificationTokenService.update(notificationToken));
    } else {
      this.subscribeToSaveResponse(this.notificationTokenService.create(notificationToken));
    }
  }

  trackProfileById(index: number, item: IProfile): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<INotificationToken>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(notificationToken: INotificationToken): void {
    this.editForm.patchValue({
      id: notificationToken.id,
      token: notificationToken.token,
      profile: notificationToken.profile,
    });

    this.profilesSharedCollection = this.profileService.addProfileToCollectionIfMissing(
      this.profilesSharedCollection,
      notificationToken.profile
    );
  }

  protected loadRelationshipsOptions(): void {
    this.profileService
      .query()
      .pipe(map((res: HttpResponse<IProfile[]>) => res.body ?? []))
      .pipe(
        map((profiles: IProfile[]) => this.profileService.addProfileToCollectionIfMissing(profiles, this.editForm.get('profile')!.value))
      )
      .subscribe((profiles: IProfile[]) => (this.profilesSharedCollection = profiles));
  }

  protected createFromForm(): INotificationToken {
    return {
      ...new NotificationToken(),
      id: this.editForm.get(['id'])!.value,
      token: this.editForm.get(['token'])!.value,
      profile: this.editForm.get(['profile'])!.value,
    };
  }
}
