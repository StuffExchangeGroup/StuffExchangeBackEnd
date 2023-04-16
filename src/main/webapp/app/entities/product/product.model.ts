import dayjs from 'dayjs/esm';
import { IRating } from 'app/entities/rating/rating.model';
import { IImage } from 'app/entities/image/image.model';
import { IExchange } from 'app/entities/exchange/exchange.model';
import { IProductCategory } from 'app/entities/product-category/product-category.model';
import { IFavorite } from 'app/entities/favorite/favorite.model';
import { IAuction } from 'app/entities/auction/auction.model';
import { IComment } from 'app/entities/comment/comment.model';
import { IProductPurpose } from 'app/entities/product-purpose/product-purpose.model';
import { ICity } from 'app/entities/city/city.model';
import { IProfile } from 'app/entities/profile/profile.model';
import { ProductStatus } from 'app/entities/enumerations/product-status.model';
import { Condition } from 'app/entities/enumerations/condition.model';

export interface IProduct {
  id?: number;
  name?: string | null;
  description?: string | null;
  notice?: string | null;
  location?: string | null;
  verifyPhone?: string | null;
  status?: ProductStatus | null;
  active?: boolean | null;
  thumbnail?: string | null;
  point?: number | null;
  favoriteCount?: number | null;
  latitude?: number | null;
  longitude?: number | null;
  condition?: Condition | null;
  requestCount?: number | null;
  receiveCount?: number | null;
  isSwapAvailable?: boolean | null;
  auctionTime?: dayjs.Dayjs | null;
  rating?: IRating | null;
  images?: IImage[] | null;
  sendExchanges?: IExchange[] | null;
  receiveExchanges?: IExchange[] | null;
  productCategories?: IProductCategory[] | null;
  favorites?: IFavorite[] | null;
  auctions?: IAuction[] | null;
  comments?: IComment[] | null;
  purposes?: IProductPurpose[] | null;
  city?: ICity | null;
  profile?: IProfile | null;
}

export class Product implements IProduct {
  constructor(
    public id?: number,
    public name?: string | null,
    public description?: string | null,
    public notice?: string | null,
    public location?: string | null,
    public verifyPhone?: string | null,
    public status?: ProductStatus | null,
    public active?: boolean | null,
    public thumbnail?: string | null,
    public point?: number | null,
    public favoriteCount?: number | null,
    public latitude?: number | null,
    public longitude?: number | null,
    public condition?: Condition | null,
    public requestCount?: number | null,
    public receiveCount?: number | null,
    public isSwapAvailable?: boolean | null,
    public auctionTime?: dayjs.Dayjs | null,
    public rating?: IRating | null,
    public images?: IImage[] | null,
    public sendExchanges?: IExchange[] | null,
    public receiveExchanges?: IExchange[] | null,
    public productCategories?: IProductCategory[] | null,
    public favorites?: IFavorite[] | null,
    public auctions?: IAuction[] | null,
    public comments?: IComment[] | null,
    public purposes?: IProductPurpose[] | null,
    public city?: ICity | null,
    public profile?: IProfile | null
  ) {
    this.active = this.active ?? false;
    this.isSwapAvailable = this.isSwapAvailable ?? false;
  }
}

export function getProductIdentifier(product: IProduct): number | undefined {
  return product.id;
}
