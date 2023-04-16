import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { NationalityService } from '../service/nationality.service';
import { INationality, Nationality } from '../nationality.model';

import { NationalityUpdateComponent } from './nationality-update.component';

describe('Nationality Management Update Component', () => {
  let comp: NationalityUpdateComponent;
  let fixture: ComponentFixture<NationalityUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let nationalityService: NationalityService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [NationalityUpdateComponent],
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
      .overrideTemplate(NationalityUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(NationalityUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    nationalityService = TestBed.inject(NationalityService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const nationality: INationality = { id: 456 };

      activatedRoute.data = of({ nationality });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(nationality));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Nationality>>();
      const nationality = { id: 123 };
      jest.spyOn(nationalityService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ nationality });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: nationality }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(nationalityService.update).toHaveBeenCalledWith(nationality);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Nationality>>();
      const nationality = new Nationality();
      jest.spyOn(nationalityService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ nationality });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: nationality }));
      saveSubject.complete();

      // THEN
      expect(nationalityService.create).toHaveBeenCalledWith(nationality);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Nationality>>();
      const nationality = { id: 123 };
      jest.spyOn(nationalityService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ nationality });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(nationalityService.update).toHaveBeenCalledWith(nationality);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
