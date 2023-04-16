import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProductPurpose } from '../product-purpose.model';

@Component({
  selector: 'jhi-product-purpose-detail',
  templateUrl: './product-purpose-detail.component.html',
})
export class ProductPurposeDetailComponent implements OnInit {
  productPurpose: IProductPurpose | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ productPurpose }) => {
      this.productPurpose = productPurpose;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
