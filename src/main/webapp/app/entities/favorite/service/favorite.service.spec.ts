import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IFavorite, Favorite } from '../favorite.model';

import { FavoriteService } from './favorite.service';

describe('Favorite Service', () => {
  let service: FavoriteService;
  let httpMock: HttpTestingController;
  let elemDefault: IFavorite;
  let expectedResult: IFavorite | IFavorite[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(FavoriteService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      createdDate: currentDate,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          createdDate: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Favorite', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          createdDate: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          createdDate: currentDate,
        },
        returnedFromService
      );

      service.create(new Favorite()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Favorite', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          createdDate: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          createdDate: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Favorite', () => {
      const patchObject = Object.assign(
        {
          createdDate: currentDate.format(DATE_TIME_FORMAT),
        },
        new Favorite()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          createdDate: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Favorite', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          createdDate: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          createdDate: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Favorite', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addFavoriteToCollectionIfMissing', () => {
      it('should add a Favorite to an empty array', () => {
        const favorite: IFavorite = { id: 123 };
        expectedResult = service.addFavoriteToCollectionIfMissing([], favorite);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(favorite);
      });

      it('should not add a Favorite to an array that contains it', () => {
        const favorite: IFavorite = { id: 123 };
        const favoriteCollection: IFavorite[] = [
          {
            ...favorite,
          },
          { id: 456 },
        ];
        expectedResult = service.addFavoriteToCollectionIfMissing(favoriteCollection, favorite);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Favorite to an array that doesn't contain it", () => {
        const favorite: IFavorite = { id: 123 };
        const favoriteCollection: IFavorite[] = [{ id: 456 }];
        expectedResult = service.addFavoriteToCollectionIfMissing(favoriteCollection, favorite);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(favorite);
      });

      it('should add only unique Favorite to an array', () => {
        const favoriteArray: IFavorite[] = [{ id: 123 }, { id: 456 }, { id: 54237 }];
        const favoriteCollection: IFavorite[] = [{ id: 123 }];
        expectedResult = service.addFavoriteToCollectionIfMissing(favoriteCollection, ...favoriteArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const favorite: IFavorite = { id: 123 };
        const favorite2: IFavorite = { id: 456 };
        expectedResult = service.addFavoriteToCollectionIfMissing([], favorite, favorite2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(favorite);
        expect(expectedResult).toContain(favorite2);
      });

      it('should accept null and undefined values', () => {
        const favorite: IFavorite = { id: 123 };
        expectedResult = service.addFavoriteToCollectionIfMissing([], null, favorite, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(favorite);
      });

      it('should return initial array if no Favorite is added', () => {
        const favoriteCollection: IFavorite[] = [{ id: 123 }];
        expectedResult = service.addFavoriteToCollectionIfMissing(favoriteCollection, undefined, null);
        expect(expectedResult).toEqual(favoriteCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
