import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IAuction, Auction } from '../auction.model';

import { AuctionService } from './auction.service';

describe('Auction Service', () => {
  let service: AuctionService;
  let httpMock: HttpTestingController;
  let elemDefault: IAuction;
  let expectedResult: IAuction | IAuction[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(AuctionService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      point: 0,
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

    it('should create a Auction', () => {
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

      service.create(new Auction()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Auction', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          point: 1,
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

    it('should partial update a Auction', () => {
      const patchObject = Object.assign({}, new Auction());

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

    it('should return a list of Auction', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          point: 1,
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

    it('should delete a Auction', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addAuctionToCollectionIfMissing', () => {
      it('should add a Auction to an empty array', () => {
        const auction: IAuction = { id: 123 };
        expectedResult = service.addAuctionToCollectionIfMissing([], auction);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(auction);
      });

      it('should not add a Auction to an array that contains it', () => {
        const auction: IAuction = { id: 123 };
        const auctionCollection: IAuction[] = [
          {
            ...auction,
          },
          { id: 456 },
        ];
        expectedResult = service.addAuctionToCollectionIfMissing(auctionCollection, auction);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Auction to an array that doesn't contain it", () => {
        const auction: IAuction = { id: 123 };
        const auctionCollection: IAuction[] = [{ id: 456 }];
        expectedResult = service.addAuctionToCollectionIfMissing(auctionCollection, auction);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(auction);
      });

      it('should add only unique Auction to an array', () => {
        const auctionArray: IAuction[] = [{ id: 123 }, { id: 456 }, { id: 28180 }];
        const auctionCollection: IAuction[] = [{ id: 123 }];
        expectedResult = service.addAuctionToCollectionIfMissing(auctionCollection, ...auctionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const auction: IAuction = { id: 123 };
        const auction2: IAuction = { id: 456 };
        expectedResult = service.addAuctionToCollectionIfMissing([], auction, auction2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(auction);
        expect(expectedResult).toContain(auction2);
      });

      it('should accept null and undefined values', () => {
        const auction: IAuction = { id: 123 };
        expectedResult = service.addAuctionToCollectionIfMissing([], null, auction, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(auction);
      });

      it('should return initial array if no Auction is added', () => {
        const auctionCollection: IAuction[] = [{ id: 123 }];
        expectedResult = service.addAuctionToCollectionIfMissing(auctionCollection, undefined, null);
        expect(expectedResult).toEqual(auctionCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
