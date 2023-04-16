import { IProduct } from 'app/entities/product/product.model';
import { ICategory } from 'app/entities/category/category.model';

export interface IProductCategory {
  id?: number;
  note?: string | null;
  product?: IProduct | null;
  category?: ICategory | null;
}

export class ProductCategory implements IProductCategory {
  constructor(public id?: number, public note?: string | null, public product?: IProduct | null, public category?: ICategory | null) {}
}

export function getProductCategoryIdentifier(productCategory: IProductCategory): number | undefined {
  return productCategory.id;
}
