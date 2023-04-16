import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { Purpose } from 'app/entities/enumerations/purpose.model';
import { IProductPurpose, ProductPurpose } from '../product-purpose.model';

import { ProductPurposeService } from './product-purpose.service';

describe('ProductPurpose Service', () => {
  let service: ProductPurposeService;
  let httpMock: HttpTestingController;
  let elemDefault: IProductPurpose;
  let expectedResult: IProductPurpose | IProductPurpose[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProductPurposeService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: Purpose.EXCHANGE,
      description: 'AAAAAAA',
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

    it('should create a ProductPurpose', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new ProductPurpose()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ProductPurpose', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          description: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ProductPurpose', () => {
      const patchObject = Object.assign({}, new ProductPurpose());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ProductPurpose', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          description: 'BBBBBB',
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

    it('should delete a ProductPurpose', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addProductPurposeToCollectionIfMissing', () => {
      it('should add a ProductPurpose to an empty array', () => {
        const productPurpose: IProductPurpose = { id: 123 };
        expectedResult = service.addProductPurposeToCollectionIfMissing([], productPurpose);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(productPurpose);
      });

      it('should not add a ProductPurpose to an array that contains it', () => {
        const productPurpose: IProductPurpose = { id: 123 };
        const productPurposeCollection: IProductPurpose[] = [
          {
            ...productPurpose,
          },
          { id: 456 },
        ];
        expectedResult = service.addProductPurposeToCollectionIfMissing(productPurposeCollection, productPurpose);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ProductPurpose to an array that doesn't contain it", () => {
        const productPurpose: IProductPurpose = { id: 123 };
        const productPurposeCollection: IProductPurpose[] = [{ id: 456 }];
        expectedResult = service.addProductPurposeToCollectionIfMissing(productPurposeCollection, productPurpose);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(productPurpose);
      });

      it('should add only unique ProductPurpose to an array', () => {
        const productPurposeArray: IProductPurpose[] = [{ id: 123 }, { id: 456 }, { id: 29224 }];
        const productPurposeCollection: IProductPurpose[] = [{ id: 123 }];
        expectedResult = service.addProductPurposeToCollectionIfMissing(productPurposeCollection, ...productPurposeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const productPurpose: IProductPurpose = { id: 123 };
        const productPurpose2: IProductPurpose = { id: 456 };
        expectedResult = service.addProductPurposeToCollectionIfMissing([], productPurpose, productPurpose2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(productPurpose);
        expect(expectedResult).toContain(productPurpose2);
      });

      it('should accept null and undefined values', () => {
        const productPurpose: IProductPurpose = { id: 123 };
        expectedResult = service.addProductPurposeToCollectionIfMissing([], null, productPurpose, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(productPurpose);
      });

      it('should return initial array if no ProductPurpose is added', () => {
        const productPurposeCollection: IProductPurpose[] = [{ id: 123 }];
        expectedResult = service.addProductPurposeToCollectionIfMissing(productPurposeCollection, undefined, null);
        expect(expectedResult).toEqual(productPurposeCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
