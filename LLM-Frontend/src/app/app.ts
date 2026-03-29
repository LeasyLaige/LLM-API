import { Component, signal, ElementRef, ViewChild, AfterViewChecked } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { LlmService } from './services/llm.service';

export interface ChatMessage {
  role: 'user' | 'assistant' | 'error';
  content: string;
}

@Component({
  selector: 'app-root',
  imports: [FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App implements AfterViewChecked {
  @ViewChild('scrollContainer') private scrollContainer!: ElementRef<HTMLElement>;

  prompt = '';
  readonly messages = signal<ChatMessage[]>([]);
  readonly loading = signal(false);
  private shouldScroll = false;

  constructor(private readonly llmService: LlmService) {}

  ngAfterViewChecked(): void {
    if (this.shouldScroll) {
      this.scrollToBottom();
      this.shouldScroll = false;
    }
  }

  onSubmit(): void {
    const trimmed = this.prompt.trim();
    if (!trimmed) return;

    // Add user message
    this.messages.update(msgs => [...msgs, { role: 'user', content: trimmed }]);
    this.prompt = '';
    this.loading.set(true);
    this.shouldScroll = true;

    this.llmService.ask(trimmed).subscribe({
      next: (res) => {
        this.messages.update(msgs => [...msgs, { role: 'assistant', content: res.response }]);
        this.loading.set(false);
        this.shouldScroll = true;
      },
      error: (err: Error) => {
        this.messages.update(msgs => [...msgs, { role: 'error', content: err.message }]);
        this.loading.set(false);
        this.shouldScroll = true;
      },
    });
  }

  onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.onSubmit();
    }
  }

  private scrollToBottom(): void {
    const el = this.scrollContainer?.nativeElement;
    if (el) {
      el.scrollTop = el.scrollHeight;
    }
  }
}
