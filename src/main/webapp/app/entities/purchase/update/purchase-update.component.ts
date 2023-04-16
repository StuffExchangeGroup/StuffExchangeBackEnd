import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IPurchase, Purchase } from '../purchase.model';
import { PurchaseService } from '../service/purchase.service';
import { IProfile } from 'app/entities/profile/profile.model';
import { ProfileService } from 'app/entities/profile/service/profile.service';
import { PurchaseType } from 'app/entities/enumerations/purchase-type.model';
import { MoneyUnit } from 'app/entities/enumerations/money-unit.model';

@Component({
  selector: 'jhi-purchase-update',
  templateUrl: './purchase-update.component.html',
})
export class PurchaseUpdateComponent implements OnInit {
  isSaving = false;
  purchaseTypeValues = Object.keys(PurchaseType);
  moneyUnitValues = Object.keys(MoneyUnit);

  profilesSharedCollection: IProfile[] = [];

  editForm = this.fb.group({
    id: [],
    purchaseType: [],
    confirmedDate: [],
    money: [],
    unit: [],
    isConfirm: [],
    profile: [],
  });

  constructor(
    protected purchaseService: PurchaseService,
    protected profileService: ProfileService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ purchase }) => {
      if (purchase.id === undefined) {
        const today = dayjs().startOf('day');
        purchase.confirmedDate = today;
      }

      this.updateForm(purchase);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const purchase = this.createFromForm();
    if (purchase.id !== undefined) {
      this.subscribeToSaveResponse(this.purchaseService.update(purchase));
    } else {
      this.subscribeToSaveResponse(this.purchaseService.create(purchase));
    }
  }

  trackProfileById(index: number, item: IProfile): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPurchase>>): void {
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

  protected updateForm(purchase: IPurchase): void {
    this.editForm.patchValue({
      id: purchase.id,
      purchaseType: purchase.purchaseType,
      confirmedDate: purchase.confirmedDate ? purchase.confirmedDate.format(DATE_TIME_FORMAT) : null,
      money: purchase.money,
      unit: purchase.unit,
      isConfirm: purchase.isConfirm,
      profile: purchase.profile,
    });

    this.profilesSharedCollection = this.profileService.addProfileToCollectionIfMissing(this.profilesSharedCollection, purchase.profile);
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

  protected createFromForm(): IPurchase {
    return {
      ...new Purchase(),
      id: this.editForm.get(['id'])!.value,
      purchaseType: this.editForm.get(['purchaseType'])!.value,
      confirmedDate: this.editForm.get(['confirmedDate'])!.value
        ? dayjs(this.editForm.get(['confirmedDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      money: this.editForm.get(['money'])!.value,
      unit: this.editForm.get(['unit'])!.value,
      isConfirm: this.editForm.get(['isConfirm'])!.value,
      profile: this.editForm.get(['profile'])!.value,
    };
  }
}
