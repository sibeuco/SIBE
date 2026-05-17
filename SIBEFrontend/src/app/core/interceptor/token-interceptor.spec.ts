import { TestBed } from '@angular/core/testing';
import { HTTP_INTERCEPTORS, HttpClient, HttpErrorResponse } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { TokenInterceptor } from './token-interceptor';
import { Component } from '@angular/core';

@Component({ template: '' })
class DummyLoginComponent {}

describe('TokenInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;
  let router: Router;

  function createToken(expInSeconds: number): string {
    const header = btoa(JSON.stringify({ alg: 'HS256', typ: 'JWT' }));
    const payload = btoa(JSON.stringify({ sub: 'user', exp: expInSeconds }));
    return `${header}.${payload}.fake-signature`;
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterTestingModule.withRoutes([
          { path: 'login', component: DummyLoginComponent }
        ])
      ],
      declarations: [DummyLoginComponent],
      providers: [
        { provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true }
      ]
    });

    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    spyOn(router, 'navigate');
  });

  afterEach(() => {
    httpMock.verify();
    sessionStorage.clear();
    localStorage.clear();
  });

  it('should add token header when a valid token exists in sessionStorage', () => {
    const futureExp = Math.floor(Date.now() / 1000) + 3600;
    const token = createToken(futureExp);
    sessionStorage.setItem('Authorization', token);

    http.get('/api/test').subscribe();

    const req = httpMock.expectOne('/api/test');
    expect(req.request.headers.get('Authorization')).toBe(token);
    req.flush({});
  });

  it('should pass request without Authorization when no token exists in sessionStorage', () => {
    http.get('/api/test').subscribe();

    const req = httpMock.expectOne('/api/test');
    expect(req.request.headers.has('Authorization')).toBeFalse();
    req.flush({});
  });

  it('should redirect to /login and throw error when token is expired', () => {
    const pastExp = Math.floor(Date.now() / 1000) - 3600;
    const token = createToken(pastExp);
    sessionStorage.setItem('Authorization', token);

    http.get('/api/test').subscribe({
      error: (err) => {
        expect(err.message).toBe('Token expirado');
      }
    });

    expect(router.navigate).toHaveBeenCalledWith(['/login']);
    expect(sessionStorage.getItem('Authorization')).toBeNull();
  });

  it('should logout and redirect on 401 response', () => {
    const futureExp = Math.floor(Date.now() / 1000) + 3600;
    const token = createToken(futureExp);
    sessionStorage.setItem('Authorization', token);

    http.get('/api/test').subscribe({
      error: (err: HttpErrorResponse) => {
        expect(err.status).toBe(401);
      }
    });

    const req = httpMock.expectOne('/api/test');
    req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

    expect(router.navigate).toHaveBeenCalledWith(['/login']);
    expect(sessionStorage.getItem('Authorization')).toBeNull();
  });

  it('should rethrow non-401 errors without redirecting', () => {
    const futureExp = Math.floor(Date.now() / 1000) + 3600;
    const token = createToken(futureExp);
    sessionStorage.setItem('Authorization', token);

    http.get('/api/test').subscribe({
      error: (err: HttpErrorResponse) => {
        expect(err.status).toBe(500);
      }
    });

    const req = httpMock.expectOne('/api/test');
    req.flush('Server Error', { status: 500, statusText: 'Internal Server Error' });

    expect(router.navigate).not.toHaveBeenCalledWith(['/login']);
  });

  it('should treat a malformed token as expired', () => {
    sessionStorage.setItem('Authorization', 'not-a-jwt');

    http.get('/api/test').subscribe({
      error: (err) => {
        expect(err.message).toBe('Token expirado');
      }
    });

    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });
});
