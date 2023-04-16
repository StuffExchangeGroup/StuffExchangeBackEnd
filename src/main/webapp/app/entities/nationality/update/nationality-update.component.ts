import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { INationality, Nationality } from '../nationality.model';
import { NationalityService } from '../service/nationality.service';

@Component({
  selector: 'jhi-nationality-update',
  templateUrl: './nationality-update.component.html',
})
export class NationalityUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [],
    code: [],
  });

  constructor(protected nationalityService: NationalityService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ nationality }) => {
      this.updateForm(nationality);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const nationality = this.createFromForm();
    if (nationality.id !== undefined) {
      this.subscribeToSaveResponse(this.nationalityService.update(nationality));
    } else {
      this.subscribeToSaveResponse(this.nationalityService.create(nationality));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<INationality>>): void {
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

  protected updateForm(nationality: INationality): void {
    this.editForm.patchValue({
      id: nationality.id,
      name: nationality.name,
      code: nationality.code,
    });
  }

  protected createFromForm(): INationality {
    return {
      ...new Nationality(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      code: this.editForm.get(['code'])!.value,
    };
  }
}
