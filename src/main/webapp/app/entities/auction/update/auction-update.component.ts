import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IAuction, Auction } from '../auction.model';
import { AuctionService } from '../service/auction.service';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';
import { IProfile } from 'app/entities/profile/profile.model';
import { ProfileService } from 'app/entities/profile/service/profile.service';

@Component({
  selector: 'jhi-auction-update',
  templateUrl: './auction-update.component.html',
})
export class AuctionUpdateComponent implements OnInit {
  isSaving = false;

  productsSharedCollection: IProduct[] = [];
  profilesSharedCollection: IProfile[] = [];

  editForm = this.fb.group({
    id: [],
    point: [],
    createdDate: [],
    product: [],
    profile: [],
  });

  constructor(
    protected auctionService: AuctionService,
    protected productService: ProductService,
    protected profileService: ProfileService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ auction }) => {
      if (auction.id === undefined) {
        const today = dayjs().startOf('day');
        auction.createdDate = today;
      }

      this.updateForm(auction);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const auction = this.createFromForm();
    if (auction.id !== undefined) {
      this.subscribeToSaveResponse(this.auctionService.update(auction));
    } else {
      this.subscribeToSaveResponse(this.auctionService.create(auction));
    }
  }

  trackProductById(index: number, item: IProduct): number {
    return item.id!;
  }

  trackProfileById(index: number, item: IProfile): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAuction>>): void {
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

  protected updateForm(auction: IAuction): void {
    this.editForm.patchValue({
      id: auction.id,
      point: auction.point,
      createdDate: auction.createdDate ? auction.createdDate.format(DATE_TIME_FORMAT) : null,
      product: auction.product,
      profile: auction.profile,
    });

    this.productsSharedCollection = this.productService.addProductToCollectionIfMissing(this.productsSharedCollection, auction.product);
    this.profilesSharedCollection = this.profileService.addProfileToCollectionIfMissing(this.profilesSharedCollection, auction.profile);
  }

  protected loadRelationshipsOptions(): void {
    this.productService
      .query()
      .pipe(map((res: HttpResponse<IProduct[]>) => res.body ?? []))
      .pipe(
        map((products: IProduct[]) => this.productService.addProductToCollectionIfMissing(products, this.editForm.get('product')!.value))
      )
      .subscribe((products: IProduct[]) => (this.productsSharedCollection = products));

    this.profileService
      .query()
      .pipe(map((res: HttpResponse<IProfile[]>) => res.body ?? []))
      .pipe(
        map((profiles: IProfile[]) => this.profileService.addProfileToCollectionIfMissing(profiles, this.editForm.get('profile')!.value))
      )
      .subscribe((profiles: IProfile[]) => (this.profilesSharedCollection = profiles));
  }

  protected createFromForm(): IAuction {
    return {
      ...new Auction(),
      id: this.editForm.get(['id'])!.value,
      point: this.editForm.get(['point'])!.value,
      createdDate: this.editForm.get(['createdDate'])!.value
        ? dayjs(this.editForm.get(['createdDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      product: this.editForm.get(['product'])!.value,
      profile: this.editForm.get(['profile'])!.value,
    };
  }
}
