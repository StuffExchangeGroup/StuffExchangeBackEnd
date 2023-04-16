import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAppConfiguration } from '../app-configuration.model';

@Component({
  selector: 'jhi-app-configuration-detail',
  templateUrl: './app-configuration-detail.component.html',
})
export class AppConfigurationDetailComponent implements OnInit {
  appConfiguration: IAppConfiguration | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ appConfiguration }) => {
      this.appConfiguration = appConfiguration;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
