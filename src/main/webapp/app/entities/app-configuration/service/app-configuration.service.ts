import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAppConfiguration, getAppConfigurationIdentifier } from '../app-configuration.model';

export type EntityResponseType = HttpResponse<IAppConfiguration>;
export type EntityArrayResponseType = HttpResponse<IAppConfiguration[]>;

@Injectable({ providedIn: 'root' })
export class AppConfigurationService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/app-configurations');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(appConfiguration: IAppConfiguration): Observable<EntityResponseType> {
    return this.http.post<IAppConfiguration>(this.resourceUrl, appConfiguration, { observe: 'response' });
  }

  update(appConfiguration: IAppConfiguration): Observable<EntityResponseType> {
    return this.http.put<IAppConfiguration>(
      `${this.resourceUrl}/${getAppConfigurationIdentifier(appConfiguration) as number}`,
      appConfiguration,
      { observe: 'response' }
    );
  }

  partialUpdate(appConfiguration: IAppConfiguration): Observable<EntityResponseType> {
    return this.http.patch<IAppConfiguration>(
      `${this.resourceUrl}/${getAppConfigurationIdentifier(appConfiguration) as number}`,
      appConfiguration,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IAppConfiguration>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAppConfiguration[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addAppConfigurationToCollectionIfMissing(
    appConfigurationCollection: IAppConfiguration[],
    ...appConfigurationsToCheck: (IAppConfiguration | null | undefined)[]
  ): IAppConfiguration[] {
    const appConfigurations: IAppConfiguration[] = appConfigurationsToCheck.filter(isPresent);
    if (appConfigurations.length > 0) {
      const appConfigurationCollectionIdentifiers = appConfigurationCollection.map(
        appConfigurationItem => getAppConfigurationIdentifier(appConfigurationItem)!
      );
      const appConfigurationsToAdd = appConfigurations.filter(appConfigurationItem => {
        const appConfigurationIdentifier = getAppConfigurationIdentifier(appConfigurationItem);
        if (appConfigurationIdentifier == null || appConfigurationCollectionIdentifiers.includes(appConfigurationIdentifier)) {
          return false;
        }
        appConfigurationCollectionIdentifiers.push(appConfigurationIdentifier);
        return true;
      });
      return [...appConfigurationsToAdd, ...appConfigurationCollection];
    }
    return appConfigurationCollection;
  }
}
