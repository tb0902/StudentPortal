import { IUser } from 'app/shared/model/user.model';

export interface IReportCard {
  id?: number;
  semester?: number;
  classification?: string;
  pdfFileContentType?: string;
  pdfFile?: string;
  comments?: string | null;
  student?: IUser | null;
  teacher?: IUser | null;
}

export const defaultValue: Readonly<IReportCard> = {};
