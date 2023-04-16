import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IFavorite, getFavoriteIdentifier } from '../favorite.model';

export type EntityResponseType = HttpResponse<IFavorite>;
export type EntityArrayResponseType = HttpResponse<IFavorite[]>;

@Injectable({ providedIn: 'root' })
export class FavoriteService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/favorites');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(favorite: IFavorite): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(favorite);
    return this.http
      .post<IFavorite>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(favorite: IFavorite): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(favorite);
    return this.http
      .put<IFavorite>(`${this.resourceUrl}/${getFavoriteIdentifier(favorite) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(favorite: IFavorite): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(favorite);
    return this.http
      .patch<IFavorite>(`${this.resourceUrl}/${getFavoriteIdentifier(favorite) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IFavorite>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IFavorite[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addFavoriteToCollectionIfMissing(favoriteCollection: IFavorite[], ...favoritesToCheck: (IFavorite | null | undefined)[]): IFavorite[] {
    const favorites: IFavorite[] = favoritesToCheck.filter(isPresent);
    if (favorites.length > 0) {
      const favoriteCollectionIdentifiers = favoriteCollection.map(favoriteItem => getFavoriteIdentifier(favoriteItem)!);
      const favoritesToAdd = favorites.filter(favoriteItem => {
        const favoriteIdentifier = getFavoriteIdentifier(favoriteItem);
        if (favoriteIdentifier == null || favoriteCollectionIdentifiers.includes(favoriteIdentifier)) {
          return false;
        }
        favoriteCollectionIdentifiers.push(favoriteIdentifier);
        return true;
      });
      return [...favoritesToAdd, ...favoriteCollection];
    }
    return favoriteCollection;
  }

  protected convertDateFromClient(favorite: IFavorite): IFavorite {
    return Object.assign({}, favorite, {
      createdDate: favorite.createdDate?.isValid() ? favorite.createdDate.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.createdDate = res.body.createdDate ? dayjs(res.body.createdDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((favorite: IFavorite) => {
        favorite.createdDate = favorite.createdDate ? dayjs(favorite.createdDate) : undefined;
      });
    }
    return res;
  }
}
