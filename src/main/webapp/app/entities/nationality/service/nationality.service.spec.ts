import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { INationality, Nationality } from '../nationality.model';

import { NationalityService } from './nationality.service';

describe('Nationality Service', () => {
  let service: NationalityService;
  let httpMock: HttpTestingController;
  let elemDefault: INationality;
  let expectedResult: INationality | INationality[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(NationalityService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      code: 'AAAAAAA',
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

    it('should create a Nationality', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Nationality()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Nationality', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          code: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Nationality', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
          code: 'BBBBBB',
        },
        new Nationality()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Nationality', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          code: 'BBBBBB',
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

    it('should delete a Nationality', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addNationalityToCollectionIfMissing', () => {
      it('should add a Nationality to an empty array', () => {
        const nationality: INationality = { id: 123 };
        expectedResult = service.addNationalityToCollectionIfMissing([], nationality);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(nationality);
      });

      it('should not add a Nationality to an array that contains it', () => {
        const nationality: INationality = { id: 123 };
        const nationalityCollection: INationality[] = [
          {
            ...nationality,
          },
          { id: 456 },
        ];
        expectedResult = service.addNationalityToCollectionIfMissing(nationalityCollection, nationality);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Nationality to an array that doesn't contain it", () => {
        const nationality: INationality = { id: 123 };
        const nationalityCollection: INationality[] = [{ id: 456 }];
        expectedResult = service.addNationalityToCollectionIfMissing(nationalityCollection, nationality);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(nationality);
      });

      it('should add only unique Nationality to an array', () => {
        const nationalityArray: INationality[] = [{ id: 123 }, { id: 456 }, { id: 76462 }];
        const nationalityCollection: INationality[] = [{ id: 123 }];
        expectedResult = service.addNationalityToCollectionIfMissing(nationalityCollection, ...nationalityArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const nationality: INationality = { id: 123 };
        const nationality2: INationality = { id: 456 };
        expectedResult = service.addNationalityToCollectionIfMissing([], nationality, nationality2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(nationality);
        expect(expectedResult).toContain(nationality2);
      });

      it('should accept null and undefined values', () => {
        const nationality: INationality = { id: 123 };
        expectedResult = service.addNationalityToCollectionIfMissing([], null, nationality, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(nationality);
      });

      it('should return initial array if no Nationality is added', () => {
        const nationalityCollection: INationality[] = [{ id: 123 }];
        expectedResult = service.addNationalityToCollectionIfMissing(nationalityCollection, undefined, null);
        expect(expectedResult).toEqual(nationalityCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
