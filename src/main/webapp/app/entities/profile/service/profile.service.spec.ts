import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IProfile, Profile } from '../profile.model';

import { ProfileService } from './profile.service';

describe('Profile Service', () => {
  let service: ProfileService;
  let httpMock: HttpTestingController;
  let elemDefault: IProfile;
  let expectedResult: IProfile | IProfile[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProfileService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      displayName: 'AAAAAAA',
      balance: 0,
      latitude: 0,
      longitude: 0,
      avatar: 'AAAAAAA',
      phone: 'AAAAAAA',
      dob: currentDate,
      location: 'AAAAAAA',
      countryCode: 'AAAAAAA',
      point: 0,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          dob: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Profile', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          dob: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dob: currentDate,
        },
        returnedFromService
      );

      service.create(new Profile()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Profile', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          displayName: 'BBBBBB',
          balance: 1,
          latitude: 1,
          longitude: 1,
          avatar: 'BBBBBB',
          phone: 'BBBBBB',
          dob: currentDate.format(DATE_TIME_FORMAT),
          location: 'BBBBBB',
          countryCode: 'BBBBBB',
          point: 1,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dob: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Profile', () => {
      const patchObject = Object.assign(
        {
          latitude: 1,
          phone: 'BBBBBB',
          countryCode: 'BBBBBB',
        },
        new Profile()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          dob: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Profile', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          displayName: 'BBBBBB',
          balance: 1,
          latitude: 1,
          longitude: 1,
          avatar: 'BBBBBB',
          phone: 'BBBBBB',
          dob: currentDate.format(DATE_TIME_FORMAT),
          location: 'BBBBBB',
          countryCode: 'BBBBBB',
          point: 1,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dob: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Profile', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addProfileToCollectionIfMissing', () => {
      it('should add a Profile to an empty array', () => {
        const profile: IProfile = { id: 123 };
        expectedResult = service.addProfileToCollectionIfMissing([], profile);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(profile);
      });

      it('should not add a Profile to an array that contains it', () => {
        const profile: IProfile = { id: 123 };
        const profileCollection: IProfile[] = [
          {
            ...profile,
          },
          { id: 456 },
        ];
        expectedResult = service.addProfileToCollectionIfMissing(profileCollection, profile);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Profile to an array that doesn't contain it", () => {
        const profile: IProfile = { id: 123 };
        const profileCollection: IProfile[] = [{ id: 456 }];
        expectedResult = service.addProfileToCollectionIfMissing(profileCollection, profile);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(profile);
      });

      it('should add only unique Profile to an array', () => {
        const profileArray: IProfile[] = [{ id: 123 }, { id: 456 }, { id: 62957 }];
        const profileCollection: IProfile[] = [{ id: 123 }];
        expectedResult = service.addProfileToCollectionIfMissing(profileCollection, ...profileArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const profile: IProfile = { id: 123 };
        const profile2: IProfile = { id: 456 };
        expectedResult = service.addProfileToCollectionIfMissing([], profile, profile2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(profile);
        expect(expectedResult).toContain(profile2);
      });

      it('should accept null and undefined values', () => {
        const profile: IProfile = { id: 123 };
        expectedResult = service.addProfileToCollectionIfMissing([], null, profile, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(profile);
      });

      it('should return initial array if no Profile is added', () => {
        const profileCollection: IProfile[] = [{ id: 123 }];
        expectedResult = service.addProfileToCollectionIfMissing(profileCollection, undefined, null);
        expect(expectedResult).toEqual(profileCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
