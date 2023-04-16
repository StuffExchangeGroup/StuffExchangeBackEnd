import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IAppConfiguration, AppConfiguration } from '../app-configuration.model';

import { AppConfigurationService } from './app-configuration.service';

describe('AppConfiguration Service', () => {
  let service: AppConfigurationService;
  let httpMock: HttpTestingController;
  let elemDefault: IAppConfiguration;
  let expectedResult: IAppConfiguration | IAppConfiguration[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(AppConfigurationService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      key: 'AAAAAAA',
      value: 'AAAAAAA',
      note: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a AppConfiguration', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new AppConfiguration()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a AppConfiguration', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          key: 'BBBBBB',
          value: 'BBBBBB',
          note: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a AppConfiguration', () => {
      const patchObject = Object.assign(
        {
          key: 'BBBBBB',
          value: 'BBBBBB',
          note: 'BBBBBB',
        },
        new AppConfiguration()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of AppConfiguration', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          key: 'BBBBBB',
          value: 'BBBBBB',
          note: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a AppConfiguration', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addAppConfigurationToCollectionIfMissing', () => {
      it('should add a AppConfiguration to an empty array', () => {
        const appConfiguration: IAppConfiguration = { id: 123 };
        expectedResult = service.addAppConfigurationToCollectionIfMissing([], appConfiguration);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(appConfiguration);
      });

      it('should not add a AppConfiguration to an array that contains it', () => {
        const appConfiguration: IAppConfiguration = { id: 123 };
        const appConfigurationCollection: IAppConfiguration[] = [
          {
            ...appConfiguration,
          },
          { id: 456 },
        ];
        expectedResult = service.addAppConfigurationToCollectionIfMissing(appConfigurationCollection, appConfiguration);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a AppConfiguration to an array that doesn't contain it", () => {
        const appConfiguration: IAppConfiguration = { id: 123 };
        const appConfigurationCollection: IAppConfiguration[] = [{ id: 456 }];
        expectedResult = service.addAppConfigurationToCollectionIfMissing(appConfigurationCollection, appConfiguration);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(appConfiguration);
      });

      it('should add only unique AppConfiguration to an array', () => {
        const appConfigurationArray: IAppConfiguration[] = [{ id: 123 }, { id: 456 }, { id: 3280 }];
        const appConfigurationCollection: IAppConfiguration[] = [{ id: 123 }];
        expectedResult = service.addAppConfigurationToCollectionIfMissing(appConfigurationCollection, ...appConfigurationArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const appConfiguration: IAppConfiguration = { id: 123 };
        const appConfiguration2: IAppConfiguration = { id: 456 };
        expectedResult = service.addAppConfigurationToCollectionIfMissing([], appConfiguration, appConfiguration2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(appConfiguration);
        expect(expectedResult).toContain(appConfiguration2);
      });

      it('should accept null and undefined values', () => {
        const appConfiguration: IAppConfiguration = { id: 123 };
        expectedResult = service.addAppConfigurationToCollectionIfMissing([], null, appConfiguration, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(appConfiguration);
      });

      it('should return initial array if no AppConfiguration is added', () => {
        const appConfigurationCollection: IAppConfiguration[] = [{ id: 123 }];
        expectedResult = service.addAppConfigurationToCollectionIfMissing(appConfigurationCollection, undefined, null);
        expect(expectedResult).toEqual(appConfigurationCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
