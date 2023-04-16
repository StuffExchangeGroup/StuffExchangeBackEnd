import dayjs from 'dayjs/esm';
import { IProduct } from 'app/entities/product/product.model';
import { IProfile } from 'app/entities/profile/profile.model';

export interface IAuction {
  id?: number;
  point?: number | null;
  createdDate?: dayjs.Dayjs | null;
  product?: IProduct | null;
  profile?: IProfile | null;
}

export class Auction implements IAuction {
  constructor(
    public id?: number,
    public point?: number | null,
    public createdDate?: dayjs.Dayjs | null,
    public product?: IProduct | null,
    public profile?: IProfile | null
  ) {}
}

export function getAuctionIdentifier(auction: IAuction): number | undefined {
  return auction.id;
}
