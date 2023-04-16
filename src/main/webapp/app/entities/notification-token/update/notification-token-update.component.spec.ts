import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { NotificationTokenService } from '../service/notification-token.service';
import { INotificationToken, NotificationToken } from '../notification-token.model';
import { IProfile } from 'app/entities/profile/profile.model';
import { ProfileService } from 'app/entities/profile/service/profile.service';

import { NotificationTokenUpdateComponent } from './notification-token-update.component';

describe('NotificationToken Management Update Component', () => {
  let comp: NotificationTokenUpdateComponent;
  let fixture: ComponentFixture<NotificationTokenUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let notificationTokenService: NotificationTokenService;
  let profileService: ProfileService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [NotificationTokenUpdateComponent],
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
      .overrideTemplate(NotificationTokenUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(NotificationTokenUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    notificationTokenService = TestBed.inject(NotificationTokenService);
    profileService = TestBed.inject(ProfileService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Profile query and add missing value', () => {
      const notificationToken: INotificationToken = { id: 456 };
      const profile: IProfile = { id: 22586 };
      notificationToken.profile = profile;

      const profileCollection: IProfile[] = [{ id: 75564 }];
      jest.spyOn(profileService, 'query').mockReturnValue(of(new HttpResponse({ body: profileCollection })));
      const additionalProfiles = [profile];
      const expectedCollection: IProfile[] = [...additionalProfiles, ...profileCollection];
      jest.spyOn(profileService, 'addProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ notificationToken });
      comp.ngOnInit();

      expect(profileService.query).toHaveBeenCalled();
      expect(profileService.addProfileToCollectionIfMissing).toHaveBeenCalledWith(profileCollection, ...additionalProfiles);
      expect(comp.profilesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const notificationToken: INotificationToken = { id: 456 };
      const profile: IProfile = { id: 62633 };
      notificationToken.profile = profile;

      activatedRoute.data = of({ notificationToken });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(notificationToken));
      expect(comp.profilesSharedCollection).toContain(profile);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<NotificationToken>>();
      const notificationToken = { id: 123 };
      jest.spyOn(notificationTokenService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notificationToken });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: notificationToken }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(notificationTokenService.update).toHaveBeenCalledWith(notificationToken);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<NotificationToken>>();
      const notificationToken = new NotificationToken();
      jest.spyOn(notificationTokenService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notificationToken });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: notificationToken }));
      saveSubject.complete();

      // THEN
      expect(notificationTokenService.create).toHaveBeenCalledWith(notificationToken);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<NotificationToken>>();
      const notificationToken = { id: 123 };
      jest.spyOn(notificationTokenService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notificationToken });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(notificationTokenService.update).toHaveBeenCalledWith(notificationToken);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackProfileById', () => {
      it('Should return tracked Profile primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackProfileById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
