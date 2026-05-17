import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { UploadDatabaseService } from './upload-database.service';
import { environment } from 'src/environments/environment';

describe('UploadDatabaseService', () => {
  let service: UploadDatabaseService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UploadDatabaseService]
    });
    service = TestBed.inject(UploadDatabaseService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => expect(service).toBeTruthy());

  it('should call POST to upload employees file', () => {
    const file = new File(['test'], 'test.xlsx', { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
    service.subirArchivoEmpleados({ archivo: file }).subscribe();
    const req = httpMock.expectOne(r => r.url === `${environment.endpoint}/carga_masiva/empleados`);
    expect(req.request.method).toBe('POST');
    req.flush({});
  });

  it('should call POST to upload students file', () => {
    const file = new File(['test'], 'test.xlsx', { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
    service.subirArchivoEstudiantes({ archivo: file }).subscribe();
    const req = httpMock.expectOne(r => r.url === `${environment.endpoint}/carga_masiva/estudiantes`);
    expect(req.request.method).toBe('POST');
    req.flush({});
  });

  it('should propagate error on 401', () => {
    const file = new File(['test'], 'test.xlsx');
    let errorCaught: any;
    service.subirArchivoEmpleados({ archivo: file }).subscribe({ error: e => errorCaught = e });
    const req = httpMock.expectOne(r => r.url.includes('/empleados'));
    req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });
    expect(errorCaught).toBeTruthy();
    expect(errorCaught.status).toBe(401);
  });

  it('should propagate error on 403', () => {
    const file = new File(['test'], 'test.xlsx');
    let errorCaught: any;
    service.subirArchivoEstudiantes({ archivo: file }).subscribe({ error: e => errorCaught = e });
    const req = httpMock.expectOne(r => r.url.includes('/estudiantes'));
    req.flush('Forbidden', { status: 403, statusText: 'Forbidden' });
    expect(errorCaught).toBeTruthy();
    expect(errorCaught.status).toBe(403);
  });

  it('should propagate non-auth errors', () => {
    const file = new File(['test'], 'test.xlsx');
    let errorCaught: any;
    service.subirArchivoEmpleados({ archivo: file }).subscribe({ error: e => errorCaught = e });
    const req = httpMock.expectOne(r => r.url.includes('/empleados'));
    req.flush('Server Error', { status: 500, statusText: 'Server Error' });
    expect(errorCaught).toBeTruthy();
  });

  it('should not add manual Authorization header (relies on interceptor)', () => {
    sessionStorage.setItem('Authorization', 'my-token');
    const file = new File(['test'], 'test.xlsx');
    service.subirArchivoEmpleados({ archivo: file }).subscribe();
    const req = httpMock.expectOne(r => r.url.includes('/empleados'));
    expect(req.request.headers.has('Authorization')).toBeFalse();
    req.flush({});
    sessionStorage.removeItem('Authorization');
  });
});
