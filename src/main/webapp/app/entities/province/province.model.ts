import { ICity } from 'app/entities/city/city.model';
import { INationality } from 'app/entities/nationality/nationality.model';

export interface IProvince {
  id?: number;
  name?: string | null;
  cities?: ICity[] | null;
  nationality?: INationality | null;
}

export class Province implements IProvince {
  constructor(public id?: number, public name?: string | null, public cities?: ICity[] | null, public nationality?: INationality | null) {}
}

export function getProvinceIdentifier(province: IProvince): number | undefined {
  return province.id;
}
