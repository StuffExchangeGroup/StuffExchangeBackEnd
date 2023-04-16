import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { INotificationToken } from '../notification-token.model';

@Component({
  selector: 'jhi-notification-token-detail',
  templateUrl: './notification-token-detail.component.html',
})
export class NotificationTokenDetailComponent implements OnInit {
  notificationToken: INotificationToken | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ notificationToken }) => {
      this.notificationToken = notificationToken;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
