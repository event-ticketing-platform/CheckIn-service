import { FormEvent, useState } from 'react';

type CheckInStatus = 'VALID' | 'DUPLICATE' | 'REJECTED';

type CheckInResponse = {
  checkInId: string;
  ticketId: string;
  eventId: string;
  attendeeId: string;
  checkInTime: string;
  checkInStatus: CheckInStatus;
};

type CheckInSummaryResponse = {
  eventId: string;
  totalCheckIns: number;
  uniqueAttendees: number;
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

const uuidPattern =
  /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;

function isUuid(value: string) {
  return uuidPattern.test(value.trim());
}

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
      // keep default error payload
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
    default:
      return 'neutral';
  }
}

export default function App() {
  const [form, setForm] = useState<RequestForm>(defaultForm);
  const [created, setCreated] = useState<CheckInResponse | null>(null);
  const [summary, setSummary] = useState<CheckInSummaryResponse | null>(null);
  const [lookedUp, setLookedUp] = useState<CheckInResponse | null>(null);
  const [loading, setLoading] = useState<'create' | 'lookup' | 'summary' | null>(null);
  const [error, setError] = useState<ApiError | null>(null);

  async function handleCreate(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!isUuid(form.ticketId) || !isUuid(form.eventId) || !isUuid(form.attendeeId)) {
      setError({
        status: 400,
        error: 'Invalid input',
        message: 'Ticket ID, Event ID, and Attendee ID must be valid UUID values.',
      });
      return;
    }

    setLoading('create');
    setError(null);
    try {
      const payload = await requestJson<CheckInResponse>('/api/check-ins', {
        method: 'POST',
        body: JSON.stringify(form),
      });
      setCreated(payload);
      setLookedUp(null);
      setSummary(null);
    } catch (caughtError) {
      setError(caughtError as ApiError);
    } finally {
      setLoading(null);
    }
  }

  async function handleLookupByTicket() {
    if (!isUuid(form.ticketId)) {
      setError({
        status: 400,
        error: 'Invalid input',
        message: 'Ticket ID must be a valid UUID value.',
      });
      return;
    }

    setLoading('lookup');
    setError(null);
    try {
      const payload = await requestJson<CheckInResponse>(`/api/check-ins/tickets/${form.ticketId}`);
      setLookedUp(payload);
      setCreated(null);
      setSummary(null);
    } catch (caughtError) {
      setError(caughtError as ApiError);
      setLookedUp(null);
    } finally {
      setLoading(null);
    }
  }

  async function handleSummary() {
    if (!isUuid(form.eventId)) {
      setError({
        status: 400,
        error: 'Invalid input',
        message: 'Event ID must be a valid UUID value.',
      });
      return;
    }

    setLoading('summary');
    setError(null);
    try {
      const payload = await requestJson<CheckInSummaryResponse>(`/api/check-ins/events/${form.eventId}/summary`);
      setSummary(payload);
      setCreated(null);
      setLookedUp(null);
    } catch (caughtError) {
      setError(caughtError as ApiError);
      setSummary(null);
    } finally {
      setLoading(null);
    }
  }

  return (
    <main className="shell">
      <section className="card form-card">
        <h1>Attendee Check-in</h1>
        <p className="subtitle">Enter ticket, event, and attendee IDs to check in or verify records.</p>

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
              {loading === 'create' ? 'Checking in...' : 'Check in attendee'}
            </button>
            <button type="button" onClick={handleLookupByTicket} disabled={loading === 'lookup'}>
              {loading === 'lookup' ? 'Loading...' : 'Find ticket check-in'}
            </button>
            <button type="button" onClick={handleSummary} disabled={loading === 'summary'}>
              {loading === 'summary' ? 'Loading...' : 'Get event summary'}
            </button>
          </div>

          {error && (
            <div className="alert alert--error">
              <strong>Request failed</strong>
              <p>{error.message ?? error.error ?? 'Unknown error'}</p>
              {error.fieldErrors && (
                <ul>
                  {Object.entries(error.fieldErrors).map(([field, message]) => (
                    <li key={field}>
                      {field}: {message}
                    </li>
                  ))}
                </ul>
              )}
            </div>
          )}
        </form>
      </section>

      <section className="results-grid">
        <article className="card result-card">
          <h2>Check-in result</h2>
          {created ? (
            <dl className="record-list">
              <div>
                <dt>Status</dt>
                <dd>
                  <span className={`tone tone--${statusTone(created.checkInStatus)}`}>{created.checkInStatus}</span>
                </dd>
              </div>
              <div>
                <dt>Check-in ID</dt>
                <dd>{created.checkInId}</dd>
              </div>
              <div>
                <dt>Time</dt>
                <dd>{formatDateTime(created.checkInTime)}</dd>
              </div>
            </dl>
          ) : (
            <p className="empty-state">No check-in created yet.</p>
          )}
        </article>

        <article className="card result-card">
          <h2>Ticket lookup</h2>
          {lookedUp ? (
            <dl className="record-list">
              <div>
                <dt>Status</dt>
                <dd>
                  <span className={`tone tone--${statusTone(lookedUp.checkInStatus)}`}>{lookedUp.checkInStatus}</span>
                </dd>
              </div>
              <div>
                <dt>Check-in ID</dt>
                <dd>{lookedUp.checkInId}</dd>
              </div>
              <div>
                <dt>Time</dt>
                <dd>{formatDateTime(lookedUp.checkInTime)}</dd>
              </div>
            </dl>
          ) : (
            <p className="empty-state">No ticket lookup result yet.</p>
          )}
        </article>

        <article className="card result-card">
          <h2>Event summary</h2>
          {summary ? (
            <div className="metrics">
              <div>
                <span>Total check-ins</span>
                <strong>{summary.totalCheckIns}</strong>
              </div>
              <div>
                <span>Unique attendees</span>
                <strong>{summary.uniqueAttendees}</strong>
              </div>
              <div>
                <span>Event ID</span>
                <strong>{summary.eventId}</strong>
              </div>
            </div>
          ) : (
            <p className="empty-state">No event summary loaded yet.</p>
          )}
        </article>
      </section>
    </main>
  );
}
