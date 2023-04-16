import dayjs from 'dayjs/esm';
import { IProduct } from 'app/entities/product/product.model';
import { IProfile } from 'app/entities/profile/profile.model';

export interface IComment {
  id?: number;
  content?: string | null;
  createdDate?: dayjs.Dayjs | null;
  image?: string | null;
  product?: IProduct | null;
  profile?: IProfile | null;
}

export class Comment implements IComment {
  constructor(
    public id?: number,
    public content?: string | null,
    public createdDate?: dayjs.Dayjs | null,
    public image?: string | null,
    public product?: IProduct | null,
    public profile?: IProfile | null
  ) {}
}

export function getCommentIdentifier(comment: IComment): number | undefined {
  return comment.id;
}
