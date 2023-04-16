import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IProduct, Product } from '../product.model';
import { ProductService } from '../service/product.service';
import { IRating } from 'app/entities/rating/rating.model';
import { RatingService } from 'app/entities/rating/service/rating.service';
import { IProductPurpose } from 'app/entities/product-purpose/product-purpose.model';
import { ProductPurposeService } from 'app/entities/product-purpose/service/product-purpose.service';
import { ICity } from 'app/entities/city/city.model';
import { CityService } from 'app/entities/city/service/city.service';
import { IProfile } from 'app/entities/profile/profile.model';
import { ProfileService } from 'app/entities/profile/service/profile.service';
import { ProductStatus } from 'app/entities/enumerations/product-status.model';
import { Condition } from 'app/entities/enumerations/condition.model';

@Component({
  selector: 'jhi-product-update',
  templateUrl: './product-update.component.html',
})
export class ProductUpdateComponent implements OnInit {
  isSaving = false;
  productStatusValues = Object.keys(ProductStatus);
  conditionValues = Object.keys(Condition);

  ratingsCollection: IRating[] = [];
  productPurposesSharedCollection: IProductPurpose[] = [];
  citiesSharedCollection: ICity[] = [];
  profilesSharedCollection: IProfile[] = [];

  editForm = this.fb.group({
    id: [],
    name: [],
    description: [],
    notice: [],
    location: [],
    verifyPhone: [],
    status: [],
    active: [],
    thumbnail: [],
    point: [],
    favoriteCount: [],
    latitude: [],
    longitude: [],
    condition: [],
    requestCount: [],
    receiveCount: [],
    isSwapAvailable: [],
    auctionTime: [],
    rating: [],
    purposes: [],
    city: [],
    profile: [],
  });

  constructor(
    protected productService: ProductService,
    protected ratingService: RatingService,
    protected productPurposeService: ProductPurposeService,
    protected cityService: CityService,
    protected profileService: ProfileService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ product }) => {
      if (product.id === undefined) {
        const today = dayjs().startOf('day');
        product.auctionTime = today;
      }

      this.updateForm(product);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const product = this.createFromForm();
    if (product.id !== undefined) {
      this.subscribeToSaveResponse(this.productService.update(product));
    } else {
      this.subscribeToSaveResponse(this.productService.create(product));
    }
  }

  trackRatingById(index: number, item: IRating): number {
    return item.id!;
  }

  trackProductPurposeById(index: number, item: IProductPurpose): number {
    return item.id!;
  }

  trackCityById(index: number, item: ICity): number {
    return item.id!;
  }

  trackProfileById(index: number, item: IProfile): number {
    return item.id!;
  }

  getSelectedProductPurpose(option: IProductPurpose, selectedVals?: IProductPurpose[]): IProductPurpose {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProduct>>): void {
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

  protected updateForm(product: IProduct): void {
    this.editForm.patchValue({
      id: product.id,
      name: product.name,
      description: product.description,
      notice: product.notice,
      location: product.location,
      verifyPhone: product.verifyPhone,
      status: product.status,
      active: product.active,
      thumbnail: product.thumbnail,
      point: product.point,
      favoriteCount: product.favoriteCount,
      latitude: product.latitude,
      longitude: product.longitude,
      condition: product.condition,
      requestCount: product.requestCount,
      receiveCount: product.receiveCount,
      isSwapAvailable: product.isSwapAvailable,
      auctionTime: product.auctionTime ? product.auctionTime.format(DATE_TIME_FORMAT) : null,
      rating: product.rating,
      purposes: product.purposes,
      city: product.city,
      profile: product.profile,
    });

    this.ratingsCollection = this.ratingService.addRatingToCollectionIfMissing(this.ratingsCollection, product.rating);
    this.productPurposesSharedCollection = this.productPurposeService.addProductPurposeToCollectionIfMissing(
      this.productPurposesSharedCollection,
      ...(product.purposes ?? [])
    );
    this.citiesSharedCollection = this.cityService.addCityToCollectionIfMissing(this.citiesSharedCollection, product.city);
    this.profilesSharedCollection = this.profileService.addProfileToCollectionIfMissing(this.profilesSharedCollection, product.profile);
  }

  protected loadRelationshipsOptions(): void {
    this.ratingService
      .query({ filter: 'product-is-null' })
      .pipe(map((res: HttpResponse<IRating[]>) => res.body ?? []))
      .pipe(map((ratings: IRating[]) => this.ratingService.addRatingToCollectionIfMissing(ratings, this.editForm.get('rating')!.value)))
      .subscribe((ratings: IRating[]) => (this.ratingsCollection = ratings));

    this.productPurposeService
      .query()
      .pipe(map((res: HttpResponse<IProductPurpose[]>) => res.body ?? []))
      .pipe(
        map((productPurposes: IProductPurpose[]) =>
          this.productPurposeService.addProductPurposeToCollectionIfMissing(
            productPurposes,
            ...(this.editForm.get('purposes')!.value ?? [])
          )
        )
      )
      .subscribe((productPurposes: IProductPurpose[]) => (this.productPurposesSharedCollection = productPurposes));

    this.cityService
      .query()
      .pipe(map((res: HttpResponse<ICity[]>) => res.body ?? []))
      .pipe(map((cities: ICity[]) => this.cityService.addCityToCollectionIfMissing(cities, this.editForm.get('city')!.value)))
      .subscribe((cities: ICity[]) => (this.citiesSharedCollection = cities));

    this.profileService
      .query()
      .pipe(map((res: HttpResponse<IProfile[]>) => res.body ?? []))
      .pipe(
        map((profiles: IProfile[]) => this.profileService.addProfileToCollectionIfMissing(profiles, this.editForm.get('profile')!.value))
      )
      .subscribe((profiles: IProfile[]) => (this.profilesSharedCollection = profiles));
  }

  protected createFromForm(): IProduct {
    return {
      ...new Product(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      description: this.editForm.get(['description'])!.value,
      notice: this.editForm.get(['notice'])!.value,
      location: this.editForm.get(['location'])!.value,
      verifyPhone: this.editForm.get(['verifyPhone'])!.value,
      status: this.editForm.get(['status'])!.value,
      active: this.editForm.get(['active'])!.value,
      thumbnail: this.editForm.get(['thumbnail'])!.value,
      point: this.editForm.get(['point'])!.value,
      favoriteCount: this.editForm.get(['favoriteCount'])!.value,
      latitude: this.editForm.get(['latitude'])!.value,
      longitude: this.editForm.get(['longitude'])!.value,
      condition: this.editForm.get(['condition'])!.value,
      requestCount: this.editForm.get(['requestCount'])!.value,
      receiveCount: this.editForm.get(['receiveCount'])!.value,
      isSwapAvailable: this.editForm.get(['isSwapAvailable'])!.value,
      auctionTime: this.editForm.get(['auctionTime'])!.value
        ? dayjs(this.editForm.get(['auctionTime'])!.value, DATE_TIME_FORMAT)
        : undefined,
      rating: this.editForm.get(['rating'])!.value,
      purposes: this.editForm.get(['purposes'])!.value,
      city: this.editForm.get(['city'])!.value,
      profile: this.editForm.get(['profile'])!.value,
    };
  }
}
