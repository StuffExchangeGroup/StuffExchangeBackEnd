import { IProfile } from 'app/entities/profile/profile.model';

export interface INotificationToken {
  id?: number;
  token?: string | null;
  profile?: IProfile | null;
}

export class NotificationToken implements INotificationToken {
  constructor(public id?: number, public token?: string | null, public profile?: IProfile | null) {}
}

export function getNotificationTokenIdentifier(notificationToken: INotificationToken): number | undefined {
  return notificationToken.id;
}
