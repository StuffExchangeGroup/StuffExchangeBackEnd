import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { INotificationToken, getNotificationTokenIdentifier } from '../notification-token.model';

export type EntityResponseType = HttpResponse<INotificationToken>;
export type EntityArrayResponseType = HttpResponse<INotificationToken[]>;

@Injectable({ providedIn: 'root' })
export class NotificationTokenService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/notification-tokens');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(notificationToken: INotificationToken): Observable<EntityResponseType> {
    return this.http.post<INotificationToken>(this.resourceUrl, notificationToken, { observe: 'response' });
  }

  update(notificationToken: INotificationToken): Observable<EntityResponseType> {
    return this.http.put<INotificationToken>(
      `${this.resourceUrl}/${getNotificationTokenIdentifier(notificationToken) as number}`,
      notificationToken,
      { observe: 'response' }
    );
  }

  partialUpdate(notificationToken: INotificationToken): Observable<EntityResponseType> {
    return this.http.patch<INotificationToken>(
      `${this.resourceUrl}/${getNotificationTokenIdentifier(notificationToken) as number}`,
      notificationToken,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<INotificationToken>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<INotificationToken[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addNotificationTokenToCollectionIfMissing(
    notificationTokenCollection: INotificationToken[],
    ...notificationTokensToCheck: (INotificationToken | null | undefined)[]
  ): INotificationToken[] {
    const notificationTokens: INotificationToken[] = notificationTokensToCheck.filter(isPresent);
    if (notificationTokens.length > 0) {
      const notificationTokenCollectionIdentifiers = notificationTokenCollection.map(
        notificationTokenItem => getNotificationTokenIdentifier(notificationTokenItem)!
      );
      const notificationTokensToAdd = notificationTokens.filter(notificationTokenItem => {
        const notificationTokenIdentifier = getNotificationTokenIdentifier(notificationTokenItem);
        if (notificationTokenIdentifier == null || notificationTokenCollectionIdentifiers.includes(notificationTokenIdentifier)) {
          return false;
        }
        notificationTokenCollectionIdentifiers.push(notificationTokenIdentifier);
        return true;
      });
      return [...notificationTokensToAdd, ...notificationTokenCollection];
    }
    return notificationTokenCollection;
  }
}
