import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IExchange, getExchangeIdentifier } from '../exchange.model';

export type EntityResponseType = HttpResponse<IExchange>;
export type EntityArrayResponseType = HttpResponse<IExchange[]>;

@Injectable({ providedIn: 'root' })
export class ExchangeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/exchanges');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(exchange: IExchange): Observable<EntityResponseType> {
    return this.http.post<IExchange>(this.resourceUrl, exchange, { observe: 'response' });
  }

  update(exchange: IExchange): Observable<EntityResponseType> {
    return this.http.put<IExchange>(`${this.resourceUrl}/${getExchangeIdentifier(exchange) as number}`, exchange, { observe: 'response' });
  }

  partialUpdate(exchange: IExchange): Observable<EntityResponseType> {
    return this.http.patch<IExchange>(`${this.resourceUrl}/${getExchangeIdentifier(exchange) as number}`, exchange, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IExchange>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IExchange[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addExchangeToCollectionIfMissing(exchangeCollection: IExchange[], ...exchangesToCheck: (IExchange | null | undefined)[]): IExchange[] {
    const exchanges: IExchange[] = exchangesToCheck.filter(isPresent);
    if (exchanges.length > 0) {
      const exchangeCollectionIdentifiers = exchangeCollection.map(exchangeItem => getExchangeIdentifier(exchangeItem)!);
      const exchangesToAdd = exchanges.filter(exchangeItem => {
        const exchangeIdentifier = getExchangeIdentifier(exchangeItem);
        if (exchangeIdentifier == null || exchangeCollectionIdentifiers.includes(exchangeIdentifier)) {
          return false;
        }
        exchangeCollectionIdentifiers.push(exchangeIdentifier);
        return true;
      });
      return [...exchangesToAdd, ...exchangeCollection];
    }
    return exchangeCollection;
  }
}
