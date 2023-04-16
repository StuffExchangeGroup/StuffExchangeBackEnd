import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CategoryService } from '../service/category.service';
import { ICategory, Category } from '../category.model';
import { IFile } from 'app/entities/file/file.model';
import { FileService } from 'app/entities/file/service/file.service';

import { CategoryUpdateComponent } from './category-update.component';

describe('Category Management Update Component', () => {
  let comp: CategoryUpdateComponent;
  let fixture: ComponentFixture<CategoryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let categoryService: CategoryService;
  let fileService: FileService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CategoryUpdateComponent],
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
      .overrideTemplate(CategoryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CategoryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    categoryService = TestBed.inject(CategoryService);
    fileService = TestBed.inject(FileService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call categoryFile query and add missing value', () => {
      const category: ICategory = { id: 456 };
      const categoryFile: IFile = { id: 2538 };
      category.categoryFile = categoryFile;

      const categoryFileCollection: IFile[] = [{ id: 93866 }];
      jest.spyOn(fileService, 'query').mockReturnValue(of(new HttpResponse({ body: categoryFileCollection })));
      const expectedCollection: IFile[] = [categoryFile, ...categoryFileCollection];
      jest.spyOn(fileService, 'addFileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ category });
      comp.ngOnInit();

      expect(fileService.query).toHaveBeenCalled();
      expect(fileService.addFileToCollectionIfMissing).toHaveBeenCalledWith(categoryFileCollection, categoryFile);
      expect(comp.categoryFilesCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const category: ICategory = { id: 456 };
      const categoryFile: IFile = { id: 9279 };
      category.categoryFile = categoryFile;

      activatedRoute.data = of({ category });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(category));
      expect(comp.categoryFilesCollection).toContain(categoryFile);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Category>>();
      const category = { id: 123 };
      jest.spyOn(categoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ category });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: category }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(categoryService.update).toHaveBeenCalledWith(category);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Category>>();
      const category = new Category();
      jest.spyOn(categoryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ category });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: category }));
      saveSubject.complete();

      // THEN
      expect(categoryService.create).toHaveBeenCalledWith(category);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Category>>();
      const category = { id: 123 };
      jest.spyOn(categoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ category });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(categoryService.update).toHaveBeenCalledWith(category);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackFileById', () => {
      it('Should return tracked File primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackFileById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
