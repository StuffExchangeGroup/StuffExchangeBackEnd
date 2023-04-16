import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ILevel, getLevelIdentifier } from '../level.model';

export type EntityResponseType = HttpResponse<ILevel>;
export type EntityArrayResponseType = HttpResponse<ILevel[]>;

@Injectable({ providedIn: 'root' })
export class LevelService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/levels');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(level: ILevel): Observable<EntityResponseType> {
    return this.http.post<ILevel>(this.resourceUrl, level, { observe: 'response' });
  }

  update(level: ILevel): Observable<EntityResponseType> {
    return this.http.put<ILevel>(`${this.resourceUrl}/${getLevelIdentifier(level) as number}`, level, { observe: 'response' });
  }

  partialUpdate(level: ILevel): Observable<EntityResponseType> {
    return this.http.patch<ILevel>(`${this.resourceUrl}/${getLevelIdentifier(level) as number}`, level, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ILevel>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ILevel[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addLevelToCollectionIfMissing(levelCollection: ILevel[], ...levelsToCheck: (ILevel | null | undefined)[]): ILevel[] {
    const levels: ILevel[] = levelsToCheck.filter(isPresent);
    if (levels.length > 0) {
      const levelCollectionIdentifiers = levelCollection.map(levelItem => getLevelIdentifier(levelItem)!);
      const levelsToAdd = levels.filter(levelItem => {
        const levelIdentifier = getLevelIdentifier(levelItem);
        if (levelIdentifier == null || levelCollectionIdentifiers.includes(levelIdentifier)) {
          return false;
        }
        levelCollectionIdentifiers.push(levelIdentifier);
        return true;
      });
      return [...levelsToAdd, ...levelCollection];
    }
    return levelCollection;
  }
}
