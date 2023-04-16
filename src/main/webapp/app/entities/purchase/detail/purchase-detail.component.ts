import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPurchase } from '../purchase.model';

@Component({
  selector: 'jhi-purchase-detail',
  templateUrl: './purchase-detail.component.html',
})
export class PurchaseDetailComponent implements OnInit {
  purchase: IPurchase | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ purchase }) => {
      this.purchase = purchase;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
