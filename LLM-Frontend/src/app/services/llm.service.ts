import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { PromptRequest, PromptResponse } from '../models/prompt.model';

@Injectable({ providedIn: 'root' })
export class LlmService {
  private readonly apiUrl = 'http://localhost:8080/api/llm/ask';

  constructor(private readonly http: HttpClient) {}

  ask(prompt: string): Observable<PromptResponse> {
    const body: PromptRequest = { prompt };
    return this.http.post<PromptResponse>(this.apiUrl, body).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let message: string;

    if (error.status === 0) {
      message = 'Unable to reach the server. Please check your connection.';
    } else {
      message = `Server error ${error.status}: ${error.error?.message ?? error.statusText}`;
    }

    console.error('LlmService error:', error);
    return throwError(() => new Error(message));
  }
}
