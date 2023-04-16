import { IProduct } from 'app/entities/product/product.model';
import { IProfile } from 'app/entities/profile/profile.model';
import { ExchangeStatus } from 'app/entities/enumerations/exchange-status.model';

export interface IExchange {
  id?: number;
  active?: boolean | null;
  ownerConfirm?: boolean | null;
  exchangerConfirm?: boolean | null;
  confirmPhone?: string | null;
  chatting?: boolean | null;
  status?: ExchangeStatus | null;
  sendProduct?: IProduct | null;
  receiveProduct?: IProduct | null;
  owner?: IProfile | null;
  exchanger?: IProfile | null;
}

export class Exchange implements IExchange {
  constructor(
    public id?: number,
    public active?: boolean | null,
    public ownerConfirm?: boolean | null,
    public exchangerConfirm?: boolean | null,
    public confirmPhone?: string | null,
    public chatting?: boolean | null,
    public status?: ExchangeStatus | null,
    public sendProduct?: IProduct | null,
    public receiveProduct?: IProduct | null,
    public owner?: IProfile | null,
    public exchanger?: IProfile | null
  ) {
    this.active = this.active ?? false;
    this.ownerConfirm = this.ownerConfirm ?? false;
    this.exchangerConfirm = this.exchangerConfirm ?? false;
    this.chatting = this.chatting ?? false;
  }
}

export function getExchangeIdentifier(exchange: IExchange): number | undefined {
  return exchange.id;
}
