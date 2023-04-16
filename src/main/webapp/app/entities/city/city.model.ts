import { IProfile } from 'app/entities/profile/profile.model';
import { IProduct } from 'app/entities/product/product.model';
import { IProvince } from 'app/entities/province/province.model';

export interface ICity {
  id?: number;
  name?: string | null;
  profiles?: IProfile[] | null;
  products?: IProduct[] | null;
  province?: IProvince | null;
}

export class City implements ICity {
  constructor(
    public id?: number,
    public name?: string | null,
    public profiles?: IProfile[] | null,
    public products?: IProduct[] | null,
    public province?: IProvince | null
  ) {}
}

export function getCityIdentifier(city: ICity): number | undefined {
  return city.id;
}
