import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IProvince, Province } from '../province.model';
import { ProvinceService } from '../service/province.service';
import { INationality } from 'app/entities/nationality/nationality.model';
import { NationalityService } from 'app/entities/nationality/service/nationality.service';

@Component({
  selector: 'jhi-province-update',
  templateUrl: './province-update.component.html',
})
export class ProvinceUpdateComponent implements OnInit {
  isSaving = false;

  nationalitiesSharedCollection: INationality[] = [];

  editForm = this.fb.group({
    id: [],
    name: [],
    nationality: [],
  });

  constructor(
    protected provinceService: ProvinceService,
    protected nationalityService: NationalityService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ province }) => {
      this.updateForm(province);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const province = this.createFromForm();
    if (province.id !== undefined) {
      this.subscribeToSaveResponse(this.provinceService.update(province));
    } else {
      this.subscribeToSaveResponse(this.provinceService.create(province));
    }
  }

  trackNationalityById(index: number, item: INationality): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProvince>>): void {
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

  protected updateForm(province: IProvince): void {
    this.editForm.patchValue({
      id: province.id,
      name: province.name,
      nationality: province.nationality,
    });

    this.nationalitiesSharedCollection = this.nationalityService.addNationalityToCollectionIfMissing(
      this.nationalitiesSharedCollection,
      province.nationality
    );
  }

  protected loadRelationshipsOptions(): void {
    this.nationalityService
      .query()
      .pipe(map((res: HttpResponse<INationality[]>) => res.body ?? []))
      .pipe(
        map((nationalities: INationality[]) =>
          this.nationalityService.addNationalityToCollectionIfMissing(nationalities, this.editForm.get('nationality')!.value)
        )
      )
      .subscribe((nationalities: INationality[]) => (this.nationalitiesSharedCollection = nationalities));
  }

  protected createFromForm(): IProvince {
    return {
      ...new Province(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      nationality: this.editForm.get(['nationality'])!.value,
    };
  }
}
