import { IProfile } from 'app/entities/profile/profile.model';

export interface ILevel {
  id?: number;
  name?: string | null;
  swapLimit?: number | null;
  profiles?: IProfile[] | null;
}

export class Level implements ILevel {
  constructor(public id?: number, public name?: string | null, public swapLimit?: number | null, public profiles?: IProfile[] | null) {}
}

export function getLevelIdentifier(level: ILevel): number | undefined {
  return level.id;
}
