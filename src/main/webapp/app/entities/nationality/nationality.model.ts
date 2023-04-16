import { IProvince } from 'app/entities/province/province.model';

export interface INationality {
  id?: number;
  name?: string | null;
  code?: string | null;
  provinces?: IProvince[] | null;
}

export class Nationality implements INationality {
  constructor(public id?: number, public name?: string | null, public code?: string | null, public provinces?: IProvince[] | null) {}
}

export function getNationalityIdentifier(nationality: INationality): number | undefined {
  return nationality.id;
}
