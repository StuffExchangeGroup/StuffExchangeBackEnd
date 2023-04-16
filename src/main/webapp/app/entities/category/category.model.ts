import { IFile } from 'app/entities/file/file.model';
import { IProductCategory } from 'app/entities/product-category/product-category.model';

export interface ICategory {
  id?: number;
  name?: string | null;
  description?: string | null;
  thumbnail?: string | null;
  active?: boolean | null;
  categoryFile?: IFile | null;
  productCategories?: IProductCategory[] | null;
}

export class Category implements ICategory {
  constructor(
    public id?: number,
    public name?: string | null,
    public description?: string | null,
    public thumbnail?: string | null,
    public active?: boolean | null,
    public categoryFile?: IFile | null,
    public productCategories?: IProductCategory[] | null
  ) {
    this.active = this.active ?? false;
  }
}

export function getCategoryIdentifier(category: ICategory): number | undefined {
  return category.id;
}
