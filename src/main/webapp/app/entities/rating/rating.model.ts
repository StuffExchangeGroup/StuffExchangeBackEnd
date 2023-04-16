import { IProduct } from 'app/entities/product/product.model';

export interface IRating {
  id?: number;
  content?: string | null;
  rate?: number | null;
  product?: IProduct | null;
}

export class Rating implements IRating {
  constructor(public id?: number, public content?: string | null, public rate?: number | null, public product?: IProduct | null) {}
}

export function getRatingIdentifier(rating: IRating): number | undefined {
  return rating.id;
}
