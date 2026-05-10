import { FormEvent, useState } from 'react';

type CheckInStatus = 'VALID' | 'DUPLICATE' | 'REJECTED' | 'REVERSED';

type CheckInResponse = {
  checkInId: string;
  ticketId: string;
  eventId: string;
  attendeeId: string;
  checkInTime: string;
  checkInStatus: CheckInStatus;
};

type ApiError = {
  status?: number;
  error?: string;
  message?: string;
  fieldErrors?: Record<string, string>;
};

type RequestForm = {
  ticketId: string;
  eventId: string;
  attendeeId: string;
};

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? '';

const defaultForm: RequestForm = {
  ticketId: '11111111-1111-1111-1111-111111111111',
  eventId: '22222222-2222-2222-2222-222222222222',
  attendeeId: '33333333-3333-3333-3333-333333333333',
};

async function requestJson<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(init?.headers ?? {}),
    },
    ...init,
  });

  if (!response.ok) {
    let errorPayload: ApiError = { status: response.status, error: response.statusText };
    try {
      errorPayload = { ...errorPayload, ...(await response.json()) };
    } catch {
    }
    throw errorPayload;
  }

  return response.json() as Promise<T>;
}

function formatDateTime(value: string) {
  return new Intl.DateTimeFormat('en-GB', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(value));
}

function statusTone(status: CheckInStatus) {
  switch (status) {
    case 'VALID':
      return 'good';
    case 'DUPLICATE':
      return 'warn';
    case 'REJECTED':
      return 'bad';
    case 'REVERSED':
      return 'neutral';
    default:
      return 'neutral';
  }
}

export default function App() {
  const [form, setForm] = useState<RequestForm>(defaultForm);
  const [currentCheckIn, setCurrentCheckIn] = useState<CheckInResponse | null>(null);
  const [loading, setLoading] = useState<'create' | 'lookup' | 'refresh' | 'reverse' | null>(null);
  const [error, setError] = useState<ApiError | null>(null);

  async function handleCreate(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setLoading('create');
    setError(null);
    try {
      const payload = await requestJson<CheckInResponse>('/api/checkin/checkins', {
        method: 'POST',
        body: JSON.stringify(form),
      });
      setCurrentCheckIn(payload);
    } catch (caughtError) {
      setError(caughtError as ApiError);
    } finally {
      setLoading(null);
    }
  }

  async function handleLookupByTicket() {
    setLoading('lookup');
    setError(null);
    try {
      const payload = await requestJson<CheckInResponse>(`/api/checkin/checkins/tickets/${form.ticketId}`);
      setCurrentCheckIn(payload);
    } catch (caughtError) {
      setError(caughtError as ApiError);
    } finally {
      setLoading(null);
    }
  }

  async function handleRefreshCurrent() {
    if (!currentCheckIn) return;
    setLoading('refresh');
    setError(null);
    try {
      const payload = await requestJson<CheckInResponse>(`/api/checkin/checkins/${currentCheckIn.checkInId}`);
      setCurrentCheckIn(payload);
    } catch (caughtError) {
      setError(caughtError as ApiError);
    } finally {
      setLoading(null);
    }
  }

  async function handleReverse() {
    if (!currentCheckIn) return;
    setLoading('reverse');
    setError(null);
    try {
      const payload = await requestJson<CheckInResponse>(`/api/checkin/checkins/${currentCheckIn.checkInId}/reverse`, {
        method: 'PATCH',
      });
      setCurrentCheckIn(payload);
    } catch (caughtError) {
      setError(caughtError as ApiError);
    } finally {
      setLoading(null);
    }
  }

  return (
    <main className="shell">
      <section className="card form-card">
        <h1>Event Gate Control</h1>
        <p className="subtitle">Operational dashboard for attendee check-ins and reversal.</p>

        <form onSubmit={handleCreate} className="inputs-grid">
          <label>
            Ticket ID
            <input
              value={form.ticketId}
              onChange={(event) => setForm({ ...form, ticketId: event.target.value.trim() })}
              placeholder="11111111-1111-1111-1111-111111111111"
            />
          </label>
          <label>
            Event ID
            <input
              value={form.eventId}
              onChange={(event) => setForm({ ...form, eventId: event.target.value.trim() })}
              placeholder="22222222-2222-2222-2222-222222222222"
            />
          </label>
          <label>
            Attendee ID
            <input
              value={form.attendeeId}
              onChange={(event) => setForm({ ...form, attendeeId: event.target.value.trim() })}
              placeholder="33333333-3333-3333-3333-333333333333"
            />
          </label>

          <div className="actions">
            <button className="primary" type="submit" disabled={loading === 'create'}>
              {loading === 'create' ? 'Processing...' : 'Check In'}
            </button>
            <button type="button" onClick={handleReverse} disabled={loading === 'reverse' || !currentCheckIn}>
              {loading === 'reverse' ? 'Reversing...' : 'Reverse Check-In'}
            </button>
            <button type="button" className="secondary" onClick={handleLookupByTicket} disabled={loading === 'lookup'}>
              Find Ticket
            </button>
            <button type="button" className="secondary" onClick={handleRefreshCurrent} disabled={loading === 'refresh' || !currentCheckIn}>
              Reload
            </button>
            <button 
              type="button" 
              className="secondary" 
              style={{ borderColor: 'var(--accent)', color: 'var(--accent)' }}
              onClick={() => window.open('http://localhost:5174', '_blank')}
            >
              View Analytics Dashboard ↗
            </button>
          </div>

          {error && (
            <div className="alert alert--error">
              <strong>Error:</strong> {error.message ?? error.error ?? 'Unknown error'}
            </div>
          )}
        </form>
      </section>

      <section className="results-grid">
        <article className="card result-card">
          <h2>Active Check-In Status</h2>
          {currentCheckIn ? (
            <dl className="record-list">
              <div>
                <dt>Result</dt>
                <dd>
                  <span className={`tone tone--${statusTone(currentCheckIn.checkInStatus)}`}>{currentCheckIn.checkInStatus}</span>
                </dd>
              </div>
              <div>
                <dt>Ticket ID</dt>
                <dd className="mono">{currentCheckIn.ticketId}</dd>
              </div>
              <div>
                <dt>Check-in Time</dt>
                <dd>{formatDateTime(currentCheckIn.checkInTime)}</dd>
              </div>
            </dl>
          ) : (
            <p className="empty-state">No active check-in session. Scan a ticket to begin.</p>
          )}
        </article>
      </section>
    </main>
  );
}
