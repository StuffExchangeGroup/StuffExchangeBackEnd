import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IFavorite } from '../favorite.model';

@Component({
  selector: 'jhi-favorite-detail',
  templateUrl: './favorite-detail.component.html',
})
export class FavoriteDetailComponent implements OnInit {
  favorite: IFavorite | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ favorite }) => {
      this.favorite = favorite;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
