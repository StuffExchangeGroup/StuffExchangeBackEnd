import { IFile } from 'app/entities/file/file.model';
import { IProduct } from 'app/entities/product/product.model';

export interface IImage {
  id?: number;
  link?: string | null;
  text?: string | null;
  imageFile?: IFile | null;
  product?: IProduct | null;
}

export class Image implements IImage {
  constructor(
    public id?: number,
    public link?: string | null,
    public text?: string | null,
    public imageFile?: IFile | null,
    public product?: IProduct | null
  ) {}
}

export function getImageIdentifier(image: IImage): number | undefined {
  return image.id;
}
