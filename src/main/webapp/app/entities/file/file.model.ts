export interface IFile {
  id?: number;
  fileName?: string;
  fileOnServer?: string | null;
  relativePath?: string | null;
  amazonS3Url?: string | null;
}

export class File implements IFile {
  constructor(
    public id?: number,
    public fileName?: string,
    public fileOnServer?: string | null,
    public relativePath?: string | null,
    public amazonS3Url?: string | null
  ) {}
}

export function getFileIdentifier(file: IFile): number | undefined {
  return file.id;
}
