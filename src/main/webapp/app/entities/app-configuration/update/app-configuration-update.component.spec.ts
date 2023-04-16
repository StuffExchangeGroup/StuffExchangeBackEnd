import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { AppConfigurationService } from '../service/app-configuration.service';
import { IAppConfiguration, AppConfiguration } from '../app-configuration.model';

import { AppConfigurationUpdateComponent } from './app-configuration-update.component';

describe('AppConfiguration Management Update Component', () => {
  let comp: AppConfigurationUpdateComponent;
  let fixture: ComponentFixture<AppConfigurationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let appConfigurationService: AppConfigurationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [AppConfigurationUpdateComponent],
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
      .overrideTemplate(AppConfigurationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AppConfigurationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    appConfigurationService = TestBed.inject(AppConfigurationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const appConfiguration: IAppConfiguration = { id: 456 };

      activatedRoute.data = of({ appConfiguration });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(appConfiguration));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<AppConfiguration>>();
      const appConfiguration = { id: 123 };
      jest.spyOn(appConfigurationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ appConfiguration });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: appConfiguration }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(appConfigurationService.update).toHaveBeenCalledWith(appConfiguration);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<AppConfiguration>>();
      const appConfiguration = new AppConfiguration();
      jest.spyOn(appConfigurationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ appConfiguration });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: appConfiguration }));
      saveSubject.complete();

      // THEN
      expect(appConfigurationService.create).toHaveBeenCalledWith(appConfiguration);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<AppConfiguration>>();
      const appConfiguration = { id: 123 };
      jest.spyOn(appConfigurationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ appConfiguration });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(appConfigurationService.update).toHaveBeenCalledWith(appConfiguration);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
