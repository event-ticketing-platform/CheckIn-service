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
  ticketId: 'T1',
  eventId: 'E1',
  attendeeId: 'A1',
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
    case 'REVERSED':
      return 'neutral';
    default:
      return 'neutral';
  }
}

export default function App() {
  const [form, setForm] = useState<RequestForm>(defaultForm);
  const [currentCheckIn, setCurrentCheckIn] = useState<CheckInResponse | null>(null);
  const [summary, setSummary] = useState<CheckInSummaryResponse | null>(null);
  const [attendance, setAttendance] = useState<number | null>(null);
  const [loading, setLoading] = useState<'create' | 'lookup' | 'summary' | 'attendance' | 'reverse' | 'refresh' | null>(null);
  const [error, setError] = useState<ApiError | null>(null);

  async function handleCreate(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!form.ticketId || !form.eventId || !form.attendeeId) {
      setError({
        status: 400,
        error: 'Invalid input',
        message: 'Ticket ID, Event ID, and Attendee ID must be provided.',
      });
      return;
    }

    setLoading('create');
    setError(null);
    try {
      const payload = await requestJson<CheckInResponse>('/checkins', {
        method: 'POST',
        body: JSON.stringify(form),
      });
      setCurrentCheckIn(payload);
      setSummary(null);
      setAttendance(null);
    } catch (caughtError) {
      setError(caughtError as ApiError);
    } finally {
      setLoading(null);
    }
  }

  async function handleLookupByTicket() {
    if (!form.ticketId) {
      setError({
        status: 400,
        error: 'Invalid input',
        message: 'Ticket ID must be provided.',
      });
      return;
    }

    setLoading('lookup');
    setError(null);
    try {
      const payload = await requestJson<CheckInResponse>(`/checkins/tickets/${form.ticketId}`);
      setCurrentCheckIn(payload);
      setSummary(null);
      setAttendance(null);
    } catch (caughtError) {
      setError(caughtError as ApiError);
    } finally {
      setLoading(null);
    }
  }

  async function handleRefreshCurrent() {
    if (!currentCheckIn) {
      setError({
        status: 400,
        error: 'No active check-in',
        message: 'Create or look up a check-in first.',
      });
      return;
    }

    setLoading('refresh');
    setError(null);
    try {
      const payload = await requestJson<CheckInResponse>(`/checkins/${currentCheckIn.checkInId}`);
      setCurrentCheckIn(payload);
    } catch (caughtError) {
      setError(caughtError as ApiError);
    } finally {
      setLoading(null);
    }
  }

  async function handleSummary() {
    if (!form.eventId) {
      setError({
        status: 400,
        error: 'Invalid input',
        message: 'Event ID must be provided.',
      });
      return;
    }

    setLoading('summary');
    setError(null);
    try {
      const payload = await requestJson<CheckInSummaryResponse>(`/checkins/events/${form.eventId}/summary`);
      setSummary(payload);
    } catch (caughtError) {
      setError(caughtError as ApiError);
      setSummary(null);
    } finally {
      setLoading(null);
    }
  }

  async function handleAttendance() {
    if (!form.eventId) {
      setError({
        status: 400,
        error: 'Invalid input',
        message: 'Event ID must be provided.',
      });
      return;
    }

    setLoading('attendance');
    setError(null);
    try {
      const payload = await requestJson<number>(`/events/${form.eventId}/attendance`);
      setAttendance(payload);
    } catch (caughtError) {
      setError(caughtError as ApiError);
      setAttendance(null);
    } finally {
      setLoading(null);
    }
  }

  async function handleReverse() {
    if (!currentCheckIn) {
      setError({
        status: 400,
        error: 'No active check-in',
        message: 'Create or look up a check-in first.',
      });
      return;
    }

    setLoading('reverse');
    setError(null);
    try {
      const payload = await requestJson<CheckInResponse>(`/checkins/${currentCheckIn.checkInId}/reverse`, {
        method: 'PATCH',
      });
      setCurrentCheckIn(payload);
      const refreshedAttendance = await requestJson<number>(`/events/${payload.eventId}/attendance`);
      setAttendance(refreshedAttendance);
    } catch (caughtError) {
      setError(caughtError as ApiError);
    } finally {
      setLoading(null);
    }
  }

  return (
    <main className="shell">
      <section className="card form-card">
        <h1>Attendee Check-in</h1>
        <p className="subtitle">Check in attendees, inspect records, see attendance counts, and reverse mistakes from one UI.</p>

        <form onSubmit={handleCreate} className="inputs-grid">
          <label>
            Ticket ID
            <input
              value={form.ticketId}
              onChange={(event) => setForm({ ...form, ticketId: event.target.value.trim() })}
              placeholder="T1"
            />
          </label>
          <label>
            Event ID
            <input
              value={form.eventId}
              onChange={(event) => setForm({ ...form, eventId: event.target.value.trim() })}
              placeholder="E1"
            />
          </label>
          <label>
            Attendee ID
            <input
              value={form.attendeeId}
              onChange={(event) => setForm({ ...form, attendeeId: event.target.value.trim() })}
              placeholder="A1"
            />
          </label>

          <div className="actions">
            <button className="primary" type="submit" disabled={loading === 'create'}>
              {loading === 'create' ? 'Checking in...' : 'Check in attendee'}
            </button>
            <button type="button" onClick={handleLookupByTicket} disabled={loading === 'lookup'}>
              {loading === 'lookup' ? 'Loading...' : 'Find ticket check-in'}
            </button>
            <button type="button" onClick={handleRefreshCurrent} disabled={loading === 'refresh' || !currentCheckIn}>
              {loading === 'refresh' ? 'Refreshing...' : 'Reload active check-in'}
            </button>
            <button type="button" onClick={handleAttendance} disabled={loading === 'attendance'}>
              {loading === 'attendance' ? 'Loading...' : 'Get attendance'}
            </button>
            <button type="button" onClick={handleReverse} disabled={loading === 'reverse' || !currentCheckIn}>
              {loading === 'reverse' ? 'Reversing...' : 'Reverse active check-in'}
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
          <h2>Active check-in</h2>
          {currentCheckIn ? (
            <dl className="record-list">
              <div>
                <dt>Status</dt>
                <dd>
                  <span className={`tone tone--${statusTone(currentCheckIn.checkInStatus)}`}>{currentCheckIn.checkInStatus}</span>
                </dd>
              </div>
              <div>
                <dt>Check-in ID</dt>
                <dd>{currentCheckIn.checkInId}</dd>
              </div>
              <div>
                <dt>Ticket ID</dt>
                <dd>{currentCheckIn.ticketId}</dd>
              </div>
              <div>
                <dt>Event ID</dt>
                <dd>{currentCheckIn.eventId}</dd>
              </div>
              <div>
                <dt>Attendee ID</dt>
                <dd>{currentCheckIn.attendeeId}</dd>
              </div>
              <div>
                <dt>Time</dt>
                <dd>{formatDateTime(currentCheckIn.checkInTime)}</dd>
              </div>
            </dl>
          ) : (
            <p className="empty-state">Create or look up a check-in to display it here.</p>
          )}
        </article>

        <article className="card result-card">
          <h2>Attendance</h2>
          {attendance !== null ? (
            <div className="metrics metrics--single">
              <div>
                <span>Current count</span>
                <strong>{attendance}</strong>
              </div>
            </div>
          ) : (
            <p className="empty-state">Click “Get attendance” to load the current attendee count.</p>
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
