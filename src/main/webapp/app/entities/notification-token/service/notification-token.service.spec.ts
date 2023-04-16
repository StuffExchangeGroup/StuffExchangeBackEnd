import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { INotificationToken, NotificationToken } from '../notification-token.model';

import { NotificationTokenService } from './notification-token.service';

describe('NotificationToken Service', () => {
  let service: NotificationTokenService;
  let httpMock: HttpTestingController;
  let elemDefault: INotificationToken;
  let expectedResult: INotificationToken | INotificationToken[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(NotificationTokenService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      token: 'AAAAAAA',
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

    it('should create a NotificationToken', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new NotificationToken()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a NotificationToken', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          token: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a NotificationToken', () => {
      const patchObject = Object.assign({}, new NotificationToken());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of NotificationToken', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          token: 'BBBBBB',
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

    it('should delete a NotificationToken', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addNotificationTokenToCollectionIfMissing', () => {
      it('should add a NotificationToken to an empty array', () => {
        const notificationToken: INotificationToken = { id: 123 };
        expectedResult = service.addNotificationTokenToCollectionIfMissing([], notificationToken);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(notificationToken);
      });

      it('should not add a NotificationToken to an array that contains it', () => {
        const notificationToken: INotificationToken = { id: 123 };
        const notificationTokenCollection: INotificationToken[] = [
          {
            ...notificationToken,
          },
          { id: 456 },
        ];
        expectedResult = service.addNotificationTokenToCollectionIfMissing(notificationTokenCollection, notificationToken);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a NotificationToken to an array that doesn't contain it", () => {
        const notificationToken: INotificationToken = { id: 123 };
        const notificationTokenCollection: INotificationToken[] = [{ id: 456 }];
        expectedResult = service.addNotificationTokenToCollectionIfMissing(notificationTokenCollection, notificationToken);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(notificationToken);
      });

      it('should add only unique NotificationToken to an array', () => {
        const notificationTokenArray: INotificationToken[] = [{ id: 123 }, { id: 456 }, { id: 25894 }];
        const notificationTokenCollection: INotificationToken[] = [{ id: 123 }];
        expectedResult = service.addNotificationTokenToCollectionIfMissing(notificationTokenCollection, ...notificationTokenArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const notificationToken: INotificationToken = { id: 123 };
        const notificationToken2: INotificationToken = { id: 456 };
        expectedResult = service.addNotificationTokenToCollectionIfMissing([], notificationToken, notificationToken2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(notificationToken);
        expect(expectedResult).toContain(notificationToken2);
      });

      it('should accept null and undefined values', () => {
        const notificationToken: INotificationToken = { id: 123 };
        expectedResult = service.addNotificationTokenToCollectionIfMissing([], null, notificationToken, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(notificationToken);
      });

      it('should return initial array if no NotificationToken is added', () => {
        const notificationTokenCollection: INotificationToken[] = [{ id: 123 }];
        expectedResult = service.addNotificationTokenToCollectionIfMissing(notificationTokenCollection, undefined, null);
        expect(expectedResult).toEqual(notificationTokenCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
