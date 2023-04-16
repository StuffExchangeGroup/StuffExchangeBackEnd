import { IProduct } from 'app/entities/product/product.model';
import { Purpose } from 'app/entities/enumerations/purpose.model';

export interface IProductPurpose {
  id?: number;
  name?: Purpose | null;
  description?: string | null;
  products?: IProduct[] | null;
}

export class ProductPurpose implements IProductPurpose {
  constructor(public id?: number, public name?: Purpose | null, public description?: string | null, public products?: IProduct[] | null) {}
}

export function getProductPurposeIdentifier(productPurpose: IProductPurpose): number | undefined {
  return productPurpose.id;
}
