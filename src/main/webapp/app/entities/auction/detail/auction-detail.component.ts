import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAuction } from '../auction.model';

@Component({
  selector: 'jhi-auction-detail',
  templateUrl: './auction-detail.component.html',
})
export class AuctionDetailComponent implements OnInit {
  auction: IAuction | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ auction }) => {
      this.auction = auction;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
