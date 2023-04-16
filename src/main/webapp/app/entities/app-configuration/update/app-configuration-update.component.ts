import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IAppConfiguration, AppConfiguration } from '../app-configuration.model';
import { AppConfigurationService } from '../service/app-configuration.service';

@Component({
  selector: 'jhi-app-configuration-update',
  templateUrl: './app-configuration-update.component.html',
})
export class AppConfigurationUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    key: [],
    value: [],
    note: [],
  });

  constructor(
    protected appConfigurationService: AppConfigurationService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ appConfiguration }) => {
      this.updateForm(appConfiguration);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const appConfiguration = this.createFromForm();
    if (appConfiguration.id !== undefined) {
      this.subscribeToSaveResponse(this.appConfigurationService.update(appConfiguration));
    } else {
      this.subscribeToSaveResponse(this.appConfigurationService.create(appConfiguration));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAppConfiguration>>): void {
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

  protected updateForm(appConfiguration: IAppConfiguration): void {
    this.editForm.patchValue({
      id: appConfiguration.id,
      key: appConfiguration.key,
      value: appConfiguration.value,
      note: appConfiguration.note,
    });
  }

  protected createFromForm(): IAppConfiguration {
    return {
      ...new AppConfiguration(),
      id: this.editForm.get(['id'])!.value,
      key: this.editForm.get(['key'])!.value,
      value: this.editForm.get(['value'])!.value,
      note: this.editForm.get(['note'])!.value,
    };
  }
}
