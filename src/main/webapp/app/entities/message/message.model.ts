export interface IMessage {
  id?: number;
  title?: string | null;
  content?: string | null;
  read?: boolean | null;
  isDelete?: boolean | null;
}

export class Message implements IMessage {
  constructor(
    public id?: number,
    public title?: string | null,
    public content?: string | null,
    public read?: boolean | null,
    public isDelete?: boolean | null
  ) {
    this.read = this.read ?? false;
    this.isDelete = this.isDelete ?? false;
  }
}

export function getMessageIdentifier(message: IMessage): number | undefined {
  return message.id;
}
