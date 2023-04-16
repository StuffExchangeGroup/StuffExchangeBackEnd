import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AppConfigurationDetailComponent } from './app-configuration-detail.component';

describe('AppConfiguration Management Detail Component', () => {
  let comp: AppConfigurationDetailComponent;
  let fixture: ComponentFixture<AppConfigurationDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AppConfigurationDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ appConfiguration: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(AppConfigurationDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(AppConfigurationDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load appConfiguration on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.appConfiguration).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
