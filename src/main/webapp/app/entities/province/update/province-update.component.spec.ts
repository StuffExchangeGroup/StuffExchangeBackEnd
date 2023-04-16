import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProvinceService } from '../service/province.service';
import { IProvince, Province } from '../province.model';
import { INationality } from 'app/entities/nationality/nationality.model';
import { NationalityService } from 'app/entities/nationality/service/nationality.service';

import { ProvinceUpdateComponent } from './province-update.component';

describe('Province Management Update Component', () => {
  let comp: ProvinceUpdateComponent;
  let fixture: ComponentFixture<ProvinceUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let provinceService: ProvinceService;
  let nationalityService: NationalityService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProvinceUpdateComponent],
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
      .overrideTemplate(ProvinceUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProvinceUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    provinceService = TestBed.inject(ProvinceService);
    nationalityService = TestBed.inject(NationalityService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Nationality query and add missing value', () => {
      const province: IProvince = { id: 456 };
      const nationality: INationality = { id: 51625 };
      province.nationality = nationality;

      const nationalityCollection: INationality[] = [{ id: 11153 }];
      jest.spyOn(nationalityService, 'query').mockReturnValue(of(new HttpResponse({ body: nationalityCollection })));
      const additionalNationalities = [nationality];
      const expectedCollection: INationality[] = [...additionalNationalities, ...nationalityCollection];
      jest.spyOn(nationalityService, 'addNationalityToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ province });
      comp.ngOnInit();

      expect(nationalityService.query).toHaveBeenCalled();
      expect(nationalityService.addNationalityToCollectionIfMissing).toHaveBeenCalledWith(
        nationalityCollection,
        ...additionalNationalities
      );
      expect(comp.nationalitiesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const province: IProvince = { id: 456 };
      const nationality: INationality = { id: 23385 };
      province.nationality = nationality;

      activatedRoute.data = of({ province });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(province));
      expect(comp.nationalitiesSharedCollection).toContain(nationality);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Province>>();
      const province = { id: 123 };
      jest.spyOn(provinceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ province });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: province }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(provinceService.update).toHaveBeenCalledWith(province);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Province>>();
      const province = new Province();
      jest.spyOn(provinceService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ province });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: province }));
      saveSubject.complete();

      // THEN
      expect(provinceService.create).toHaveBeenCalledWith(province);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Province>>();
      const province = { id: 123 };
      jest.spyOn(provinceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ province });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(provinceService.update).toHaveBeenCalledWith(province);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackNationalityById', () => {
      it('Should return tracked Nationality primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackNationalityById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
