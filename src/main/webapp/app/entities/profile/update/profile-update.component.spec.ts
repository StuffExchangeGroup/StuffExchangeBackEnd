import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProfileService } from '../service/profile.service';
import { IProfile, Profile } from '../profile.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { ICity } from 'app/entities/city/city.model';
import { CityService } from 'app/entities/city/service/city.service';
import { ILevel } from 'app/entities/level/level.model';
import { LevelService } from 'app/entities/level/service/level.service';

import { ProfileUpdateComponent } from './profile-update.component';

describe('Profile Management Update Component', () => {
  let comp: ProfileUpdateComponent;
  let fixture: ComponentFixture<ProfileUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let profileService: ProfileService;
  let userService: UserService;
  let cityService: CityService;
  let levelService: LevelService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProfileUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ProfileUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProfileUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    profileService = TestBed.inject(ProfileService);
    userService = TestBed.inject(UserService);
    cityService = TestBed.inject(CityService);
    levelService = TestBed.inject(LevelService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const profile: IProfile = { id: 456 };
      const user: IUser = { id: 81890 };
      profile.user = user;

      const userCollection: IUser[] = [{ id: 61017 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ profile });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call City query and add missing value', () => {
      const profile: IProfile = { id: 456 };
      const city: ICity = { id: 79463 };
      profile.city = city;

      const cityCollection: ICity[] = [{ id: 60129 }];
      jest.spyOn(cityService, 'query').mockReturnValue(of(new HttpResponse({ body: cityCollection })));
      const additionalCities = [city];
      const expectedCollection: ICity[] = [...additionalCities, ...cityCollection];
      jest.spyOn(cityService, 'addCityToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ profile });
      comp.ngOnInit();

      expect(cityService.query).toHaveBeenCalled();
      expect(cityService.addCityToCollectionIfMissing).toHaveBeenCalledWith(cityCollection, ...additionalCities);
      expect(comp.citiesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Level query and add missing value', () => {
      const profile: IProfile = { id: 456 };
      const level: ILevel = { id: 48201 };
      profile.level = level;

      const levelCollection: ILevel[] = [{ id: 31619 }];
      jest.spyOn(levelService, 'query').mockReturnValue(of(new HttpResponse({ body: levelCollection })));
      const additionalLevels = [level];
      const expectedCollection: ILevel[] = [...additionalLevels, ...levelCollection];
      jest.spyOn(levelService, 'addLevelToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ profile });
      comp.ngOnInit();

      expect(levelService.query).toHaveBeenCalled();
      expect(levelService.addLevelToCollectionIfMissing).toHaveBeenCalledWith(levelCollection, ...additionalLevels);
      expect(comp.levelsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const profile: IProfile = { id: 456 };
      const user: IUser = { id: 63630 };
      profile.user = user;
      const city: ICity = { id: 56682 };
      profile.city = city;
      const level: ILevel = { id: 17560 };
      profile.level = level;

      activatedRoute.data = of({ profile });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(profile));
      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.citiesSharedCollection).toContain(city);
      expect(comp.levelsSharedCollection).toContain(level);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Profile>>();
      const profile = { id: 123 };
      jest.spyOn(profileService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: profile }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(profileService.update).toHaveBeenCalledWith(profile);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Profile>>();
      const profile = new Profile();
      jest.spyOn(profileService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: profile }));
      saveSubject.complete();

      // THEN
      expect(profileService.create).toHaveBeenCalledWith(profile);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Profile>>();
      const profile = { id: 123 };
      jest.spyOn(profileService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(profileService.update).toHaveBeenCalledWith(profile);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackUserById', () => {
      it('Should return tracked User primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackUserById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackCityById', () => {
      it('Should return tracked City primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackCityById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackLevelById', () => {
      it('Should return tracked Level primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackLevelById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
