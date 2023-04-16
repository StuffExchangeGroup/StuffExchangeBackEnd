import dayjs from 'dayjs/esm';
import { IProfile } from 'app/entities/profile/profile.model';
import { PurchaseType } from 'app/entities/enumerations/purchase-type.model';
import { MoneyUnit } from 'app/entities/enumerations/money-unit.model';

export interface IPurchase {
  id?: number;
  purchaseType?: PurchaseType | null;
  confirmedDate?: dayjs.Dayjs | null;
  money?: number | null;
  unit?: MoneyUnit | null;
  isConfirm?: boolean | null;
  profile?: IProfile | null;
}

export class Purchase implements IPurchase {
  constructor(
    public id?: number,
    public purchaseType?: PurchaseType | null,
    public confirmedDate?: dayjs.Dayjs | null,
    public money?: number | null,
    public unit?: MoneyUnit | null,
    public isConfirm?: boolean | null,
    public profile?: IProfile | null
  ) {
    this.isConfirm = this.isConfirm ?? false;
  }
}

export function getPurchaseIdentifier(purchase: IPurchase): number | undefined {
  return purchase.id;
}
