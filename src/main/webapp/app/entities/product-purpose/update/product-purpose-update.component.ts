import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IProductPurpose, ProductPurpose } from '../product-purpose.model';
import { ProductPurposeService } from '../service/product-purpose.service';
import { Purpose } from 'app/entities/enumerations/purpose.model';

@Component({
  selector: 'jhi-product-purpose-update',
  templateUrl: './product-purpose-update.component.html',
})
export class ProductPurposeUpdateComponent implements OnInit {
  isSaving = false;
  purposeValues = Object.keys(Purpose);

  editForm = this.fb.group({
    id: [],
    name: [],
    description: [],
  });

  constructor(
    protected productPurposeService: ProductPurposeService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ productPurpose }) => {
      this.updateForm(productPurpose);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const productPurpose = this.createFromForm();
    if (productPurpose.id !== undefined) {
      this.subscribeToSaveResponse(this.productPurposeService.update(productPurpose));
    } else {
      this.subscribeToSaveResponse(this.productPurposeService.create(productPurpose));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProductPurpose>>): void {
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

  protected updateForm(productPurpose: IProductPurpose): void {
    this.editForm.patchValue({
      id: productPurpose.id,
      name: productPurpose.name,
      description: productPurpose.description,
    });
  }

  protected createFromForm(): IProductPurpose {
    return {
      ...new ProductPurpose(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      description: this.editForm.get(['description'])!.value,
    };
  }
}
