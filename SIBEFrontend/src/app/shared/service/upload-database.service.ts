import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { EmployeeFileRequest } from '../model/employee.model';
import { StudentFileRequest } from '../model/student.model';

@Injectable({
  providedIn: 'root'
})
export class UploadDatabaseService {
  private readonly API_URL_BASE = `${environment.endpoint}/carga_masiva`;
  private readonly ENDPOINTS: Record<'empleados' | 'estudiantes', string> = {
    empleados: `${this.API_URL_BASE}/empleados`,
    estudiantes: `${this.API_URL_BASE}/estudiantes`
  };

  private readonly PARAM_NAME = 'archivo';

  constructor(private http: HttpClient) { }

  subirArchivoEmpleados(request: EmployeeFileRequest): Observable<any> {
    return this.subirArchivo(request.archivo, 'empleados');
  }

  subirArchivoEstudiantes(request: StudentFileRequest): Observable<any> {
    return this.subirArchivo(request.archivo, 'estudiantes');
  }

  private subirArchivo(archivo: File, tipo: 'empleados' | 'estudiantes'): Observable<any> {
    const formData = this.crearFormData(archivo);
    return this.http.post(this.ENDPOINTS[tipo], formData).pipe(
      catchError(error => throwError(() => error))
    );
  }

  private crearFormData(archivo: File): FormData {
    const formData = new FormData();
    formData.append(this.PARAM_NAME, archivo);
    return formData;
  }
}

