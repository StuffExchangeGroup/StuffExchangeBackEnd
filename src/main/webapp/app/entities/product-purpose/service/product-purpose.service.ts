import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IProductPurpose, getProductPurposeIdentifier } from '../product-purpose.model';

export type EntityResponseType = HttpResponse<IProductPurpose>;
export type EntityArrayResponseType = HttpResponse<IProductPurpose[]>;

@Injectable({ providedIn: 'root' })
export class ProductPurposeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/product-purposes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(productPurpose: IProductPurpose): Observable<EntityResponseType> {
    return this.http.post<IProductPurpose>(this.resourceUrl, productPurpose, { observe: 'response' });
  }

  update(productPurpose: IProductPurpose): Observable<EntityResponseType> {
    return this.http.put<IProductPurpose>(`${this.resourceUrl}/${getProductPurposeIdentifier(productPurpose) as number}`, productPurpose, {
      observe: 'response',
    });
  }

  partialUpdate(productPurpose: IProductPurpose): Observable<EntityResponseType> {
    return this.http.patch<IProductPurpose>(
      `${this.resourceUrl}/${getProductPurposeIdentifier(productPurpose) as number}`,
      productPurpose,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IProductPurpose>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProductPurpose[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addProductPurposeToCollectionIfMissing(
    productPurposeCollection: IProductPurpose[],
    ...productPurposesToCheck: (IProductPurpose | null | undefined)[]
  ): IProductPurpose[] {
    const productPurposes: IProductPurpose[] = productPurposesToCheck.filter(isPresent);
    if (productPurposes.length > 0) {
      const productPurposeCollectionIdentifiers = productPurposeCollection.map(
        productPurposeItem => getProductPurposeIdentifier(productPurposeItem)!
      );
      const productPurposesToAdd = productPurposes.filter(productPurposeItem => {
        const productPurposeIdentifier = getProductPurposeIdentifier(productPurposeItem);
        if (productPurposeIdentifier == null || productPurposeCollectionIdentifiers.includes(productPurposeIdentifier)) {
          return false;
        }
        productPurposeCollectionIdentifiers.push(productPurposeIdentifier);
        return true;
      });
      return [...productPurposesToAdd, ...productPurposeCollection];
    }
    return productPurposeCollection;
  }
}
