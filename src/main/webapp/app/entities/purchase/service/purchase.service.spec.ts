import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { PurchaseType } from 'app/entities/enumerations/purchase-type.model';
import { MoneyUnit } from 'app/entities/enumerations/money-unit.model';
import { IPurchase, Purchase } from '../purchase.model';

import { PurchaseService } from './purchase.service';

describe('Purchase Service', () => {
  let service: PurchaseService;
  let httpMock: HttpTestingController;
  let elemDefault: IPurchase;
  let expectedResult: IPurchase | IPurchase[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PurchaseService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      purchaseType: PurchaseType.ATM,
      confirmedDate: currentDate,
      money: 0,
      unit: MoneyUnit.VND,
      isConfirm: false,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          confirmedDate: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Purchase', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          confirmedDate: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          confirmedDate: currentDate,
        },
        returnedFromService
      );

      service.create(new Purchase()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Purchase', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          purchaseType: 'BBBBBB',
          confirmedDate: currentDate.format(DATE_TIME_FORMAT),
          money: 1,
          unit: 'BBBBBB',
          isConfirm: true,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          confirmedDate: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Purchase', () => {
      const patchObject = Object.assign(
        {
          purchaseType: 'BBBBBB',
          confirmedDate: currentDate.format(DATE_TIME_FORMAT),
          money: 1,
          isConfirm: true,
        },
        new Purchase()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          confirmedDate: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Purchase', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          purchaseType: 'BBBBBB',
          confirmedDate: currentDate.format(DATE_TIME_FORMAT),
          money: 1,
          unit: 'BBBBBB',
          isConfirm: true,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          confirmedDate: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Purchase', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addPurchaseToCollectionIfMissing', () => {
      it('should add a Purchase to an empty array', () => {
        const purchase: IPurchase = { id: 123 };
        expectedResult = service.addPurchaseToCollectionIfMissing([], purchase);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(purchase);
      });

      it('should not add a Purchase to an array that contains it', () => {
        const purchase: IPurchase = { id: 123 };
        const purchaseCollection: IPurchase[] = [
          {
            ...purchase,
          },
          { id: 456 },
        ];
        expectedResult = service.addPurchaseToCollectionIfMissing(purchaseCollection, purchase);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Purchase to an array that doesn't contain it", () => {
        const purchase: IPurchase = { id: 123 };
        const purchaseCollection: IPurchase[] = [{ id: 456 }];
        expectedResult = service.addPurchaseToCollectionIfMissing(purchaseCollection, purchase);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(purchase);
      });

      it('should add only unique Purchase to an array', () => {
        const purchaseArray: IPurchase[] = [{ id: 123 }, { id: 456 }, { id: 1486 }];
        const purchaseCollection: IPurchase[] = [{ id: 123 }];
        expectedResult = service.addPurchaseToCollectionIfMissing(purchaseCollection, ...purchaseArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const purchase: IPurchase = { id: 123 };
        const purchase2: IPurchase = { id: 456 };
        expectedResult = service.addPurchaseToCollectionIfMissing([], purchase, purchase2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(purchase);
        expect(expectedResult).toContain(purchase2);
      });

      it('should accept null and undefined values', () => {
        const purchase: IPurchase = { id: 123 };
        expectedResult = service.addPurchaseToCollectionIfMissing([], null, purchase, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(purchase);
      });

      it('should return initial array if no Purchase is added', () => {
        const purchaseCollection: IPurchase[] = [{ id: 123 }];
        expectedResult = service.addPurchaseToCollectionIfMissing(purchaseCollection, undefined, null);
        expect(expectedResult).toEqual(purchaseCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
