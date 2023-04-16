import dayjs from 'dayjs/esm';
import { IProduct } from 'app/entities/product/product.model';
import { IProfile } from 'app/entities/profile/profile.model';

export interface IFavorite {
  id?: number;
  createdDate?: dayjs.Dayjs | null;
  product?: IProduct | null;
  profile?: IProfile | null;
}

export class Favorite implements IFavorite {
  constructor(
    public id?: number,
    public createdDate?: dayjs.Dayjs | null,
    public product?: IProduct | null,
    public profile?: IProfile | null
  ) {}
}

export function getFavoriteIdentifier(favorite: IFavorite): number | undefined {
  return favorite.id;
}
