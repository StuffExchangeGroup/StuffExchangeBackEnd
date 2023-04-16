export interface IAppConfiguration {
  id?: number;
  key?: string | null;
  value?: string | null;
  note?: string | null;
}

export class AppConfiguration implements IAppConfiguration {
  constructor(public id?: number, public key?: string | null, public value?: string | null, public note?: string | null) {}
}

export function getAppConfigurationIdentifier(appConfiguration: IAppConfiguration): number | undefined {
  return appConfiguration.id;
}
