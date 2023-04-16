import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IExchange, Exchange } from '../exchange.model';
import { ExchangeService } from '../service/exchange.service';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';
import { IProfile } from 'app/entities/profile/profile.model';
import { ProfileService } from 'app/entities/profile/service/profile.service';
import { ExchangeStatus } from 'app/entities/enumerations/exchange-status.model';

@Component({
  selector: 'jhi-exchange-update',
  templateUrl: './exchange-update.component.html',
})
export class ExchangeUpdateComponent implements OnInit {
  isSaving = false;
  exchangeStatusValues = Object.keys(ExchangeStatus);

  productsSharedCollection: IProduct[] = [];
  profilesSharedCollection: IProfile[] = [];

  editForm = this.fb.group({
    id: [],
    active: [],
    ownerConfirm: [],
    exchangerConfirm: [],
    confirmPhone: [],
    chatting: [],
    status: [],
    sendProduct: [],
    receiveProduct: [],
    owner: [],
    exchanger: [],
  });

  constructor(
    protected exchangeService: ExchangeService,
    protected productService: ProductService,
    protected profileService: ProfileService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ exchange }) => {
      this.updateForm(exchange);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const exchange = this.createFromForm();
    if (exchange.id !== undefined) {
      this.subscribeToSaveResponse(this.exchangeService.update(exchange));
    } else {
      this.subscribeToSaveResponse(this.exchangeService.create(exchange));
    }
  }

  trackProductById(index: number, item: IProduct): number {
    return item.id!;
  }

  trackProfileById(index: number, item: IProfile): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IExchange>>): void {
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

  protected updateForm(exchange: IExchange): void {
    this.editForm.patchValue({
      id: exchange.id,
      active: exchange.active,
      ownerConfirm: exchange.ownerConfirm,
      exchangerConfirm: exchange.exchangerConfirm,
      confirmPhone: exchange.confirmPhone,
      chatting: exchange.chatting,
      status: exchange.status,
      sendProduct: exchange.sendProduct,
      receiveProduct: exchange.receiveProduct,
      owner: exchange.owner,
      exchanger: exchange.exchanger,
    });

    this.productsSharedCollection = this.productService.addProductToCollectionIfMissing(
      this.productsSharedCollection,
      exchange.sendProduct,
      exchange.receiveProduct
    );
    this.profilesSharedCollection = this.profileService.addProfileToCollectionIfMissing(
      this.profilesSharedCollection,
      exchange.owner,
      exchange.exchanger
    );
  }

  protected loadRelationshipsOptions(): void {
    this.productService
      .query()
      .pipe(map((res: HttpResponse<IProduct[]>) => res.body ?? []))
      .pipe(
        map((products: IProduct[]) =>
          this.productService.addProductToCollectionIfMissing(
            products,
            this.editForm.get('sendProduct')!.value,
            this.editForm.get('receiveProduct')!.value
          )
        )
      )
      .subscribe((products: IProduct[]) => (this.productsSharedCollection = products));

    this.profileService
      .query()
      .pipe(map((res: HttpResponse<IProfile[]>) => res.body ?? []))
      .pipe(
        map((profiles: IProfile[]) =>
          this.profileService.addProfileToCollectionIfMissing(
            profiles,
            this.editForm.get('owner')!.value,
            this.editForm.get('exchanger')!.value
          )
        )
      )
      .subscribe((profiles: IProfile[]) => (this.profilesSharedCollection = profiles));
  }

  protected createFromForm(): IExchange {
    return {
      ...new Exchange(),
      id: this.editForm.get(['id'])!.value,
      active: this.editForm.get(['active'])!.value,
      ownerConfirm: this.editForm.get(['ownerConfirm'])!.value,
      exchangerConfirm: this.editForm.get(['exchangerConfirm'])!.value,
      confirmPhone: this.editForm.get(['confirmPhone'])!.value,
      chatting: this.editForm.get(['chatting'])!.value,
      status: this.editForm.get(['status'])!.value,
      sendProduct: this.editForm.get(['sendProduct'])!.value,
      receiveProduct: this.editForm.get(['receiveProduct'])!.value,
      owner: this.editForm.get(['owner'])!.value,
      exchanger: this.editForm.get(['exchanger'])!.value,
    };
  }
}
