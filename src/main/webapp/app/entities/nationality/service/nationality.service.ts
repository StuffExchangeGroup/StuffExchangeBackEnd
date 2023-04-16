import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { INationality, getNationalityIdentifier } from '../nationality.model';

export type EntityResponseType = HttpResponse<INationality>;
export type EntityArrayResponseType = HttpResponse<INationality[]>;

@Injectable({ providedIn: 'root' })
export class NationalityService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/nationalities');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(nationality: INationality): Observable<EntityResponseType> {
    return this.http.post<INationality>(this.resourceUrl, nationality, { observe: 'response' });
  }

  update(nationality: INationality): Observable<EntityResponseType> {
    return this.http.put<INationality>(`${this.resourceUrl}/${getNationalityIdentifier(nationality) as number}`, nationality, {
      observe: 'response',
    });
  }

  partialUpdate(nationality: INationality): Observable<EntityResponseType> {
    return this.http.patch<INationality>(`${this.resourceUrl}/${getNationalityIdentifier(nationality) as number}`, nationality, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<INationality>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<INationality[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addNationalityToCollectionIfMissing(
    nationalityCollection: INationality[],
    ...nationalitiesToCheck: (INationality | null | undefined)[]
  ): INationality[] {
    const nationalities: INationality[] = nationalitiesToCheck.filter(isPresent);
    if (nationalities.length > 0) {
      const nationalityCollectionIdentifiers = nationalityCollection.map(nationalityItem => getNationalityIdentifier(nationalityItem)!);
      const nationalitiesToAdd = nationalities.filter(nationalityItem => {
        const nationalityIdentifier = getNationalityIdentifier(nationalityItem);
        if (nationalityIdentifier == null || nationalityCollectionIdentifiers.includes(nationalityIdentifier)) {
          return false;
        }
        nationalityCollectionIdentifiers.push(nationalityIdentifier);
        return true;
      });
      return [...nationalitiesToAdd, ...nationalityCollection];
    }
    return nationalityCollection;
  }
}
