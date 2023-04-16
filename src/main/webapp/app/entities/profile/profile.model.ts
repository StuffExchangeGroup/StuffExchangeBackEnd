import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IFavorite } from 'app/entities/favorite/favorite.model';
import { IExchange } from 'app/entities/exchange/exchange.model';
import { IPurchase } from 'app/entities/purchase/purchase.model';
import { IProduct } from 'app/entities/product/product.model';
import { INotificationToken } from 'app/entities/notification-token/notification-token.model';
import { IAuction } from 'app/entities/auction/auction.model';
import { IComment } from 'app/entities/comment/comment.model';
import { ICity } from 'app/entities/city/city.model';
import { ILevel } from 'app/entities/level/level.model';

export interface IProfile {
  id?: number;
  displayName?: string | null;
  balance?: number | null;
  latitude?: number | null;
  longitude?: number | null;
  avatar?: string | null;
  phone?: string | null;
  dob?: dayjs.Dayjs | null;
  location?: string | null;
  countryCode?: string | null;
  point?: number | null;
  user?: IUser | null;
  favorites?: IFavorite[] | null;
  ownerExchanges?: IExchange[] | null;
  exchangerExchanges?: IExchange[] | null;
  purchases?: IPurchase[] | null;
  products?: IProduct[] | null;
  notificationTokens?: INotificationToken[] | null;
  auctions?: IAuction[] | null;
  comments?: IComment[] | null;
  city?: ICity | null;
  level?: ILevel | null;
}

export class Profile implements IProfile {
  constructor(
    public id?: number,
    public displayName?: string | null,
    public balance?: number | null,
    public latitude?: number | null,
    public longitude?: number | null,
    public avatar?: string | null,
    public phone?: string | null,
    public dob?: dayjs.Dayjs | null,
    public location?: string | null,
    public countryCode?: string | null,
    public point?: number | null,
    public user?: IUser | null,
    public favorites?: IFavorite[] | null,
    public ownerExchanges?: IExchange[] | null,
    public exchangerExchanges?: IExchange[] | null,
    public purchases?: IPurchase[] | null,
    public products?: IProduct[] | null,
    public notificationTokens?: INotificationToken[] | null,
    public auctions?: IAuction[] | null,
    public comments?: IComment[] | null,
    public city?: ICity | null,
    public level?: ILevel | null
  ) {}
}

export function getProfileIdentifier(profile: IProfile): number | undefined {
  return profile.id;
}
