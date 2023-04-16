import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IImage, Image } from '../image.model';
import { ImageService } from '../service/image.service';
import { IFile } from 'app/entities/file/file.model';
import { FileService } from 'app/entities/file/service/file.service';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';

@Component({
  selector: 'jhi-image-update',
  templateUrl: './image-update.component.html',
})
export class ImageUpdateComponent implements OnInit {
  isSaving = false;

  imageFilesCollection: IFile[] = [];
  productsSharedCollection: IProduct[] = [];

  editForm = this.fb.group({
    id: [],
    link: [],
    text: [],
    imageFile: [],
    product: [],
  });

  constructor(
    protected imageService: ImageService,
    protected fileService: FileService,
    protected productService: ProductService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ image }) => {
      this.updateForm(image);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const image = this.createFromForm();
    if (image.id !== undefined) {
      this.subscribeToSaveResponse(this.imageService.update(image));
    } else {
      this.subscribeToSaveResponse(this.imageService.create(image));
    }
  }

  trackFileById(index: number, item: IFile): number {
    return item.id!;
  }

  trackProductById(index: number, item: IProduct): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IImage>>): void {
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

  protected updateForm(image: IImage): void {
    this.editForm.patchValue({
      id: image.id,
      link: image.link,
      text: image.text,
      imageFile: image.imageFile,
      product: image.product,
    });

    this.imageFilesCollection = this.fileService.addFileToCollectionIfMissing(this.imageFilesCollection, image.imageFile);
    this.productsSharedCollection = this.productService.addProductToCollectionIfMissing(this.productsSharedCollection, image.product);
  }

  protected loadRelationshipsOptions(): void {
    this.fileService
      .query({ filter: 'image-is-null' })
      .pipe(map((res: HttpResponse<IFile[]>) => res.body ?? []))
      .pipe(map((files: IFile[]) => this.fileService.addFileToCollectionIfMissing(files, this.editForm.get('imageFile')!.value)))
      .subscribe((files: IFile[]) => (this.imageFilesCollection = files));

    this.productService
      .query()
      .pipe(map((res: HttpResponse<IProduct[]>) => res.body ?? []))
      .pipe(
        map((products: IProduct[]) => this.productService.addProductToCollectionIfMissing(products, this.editForm.get('product')!.value))
      )
      .subscribe((products: IProduct[]) => (this.productsSharedCollection = products));
  }

  protected createFromForm(): IImage {
    return {
      ...new Image(),
      id: this.editForm.get(['id'])!.value,
      link: this.editForm.get(['link'])!.value,
      text: this.editForm.get(['text'])!.value,
      imageFile: this.editForm.get(['imageFile'])!.value,
      product: this.editForm.get(['product'])!.value,
    };
  }
}
