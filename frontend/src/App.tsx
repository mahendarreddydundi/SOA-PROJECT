import { useCallback, useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { Activity, ArrowRight, Bell, CirclePlus, ClipboardList, LogOut, RefreshCw, Search, ShieldCheck, UserRound, X } from 'lucide-react'
import './App.css'

type Complaint = { id: number; title: string; description: string; customerEmail: string; status: 'OPEN' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED' }
type Assignment = { id: number; complaintId: number; department: string; agent: string }
type Notification = { id: number; complaintId: number; recipient: string; message: string; sentAt: string }
type WorkspaceView = 'overview' | 'assignments' | 'activity'

const API = '/api'

function App() {
  const [token, setToken] = useState(() => localStorage.getItem('resolvenow-token'))
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [authMode, setAuthMode] = useState<'login' | 'signup'>('login')
  const [authError, setAuthError] = useState('')
  const [complaints, setComplaints] = useState<Complaint[]>([])
  const [assignments, setAssignments] = useState<Assignment[]>([])
  const [notifications, setNotifications] = useState<Notification[]>([])
  const [showNew, setShowNew] = useState(false)
  const [newComplaint, setNewComplaint] = useState({ title: '', description: '', customerEmail: '' })
  const [loading, setLoading] = useState(false)
  const [notice, setNotice] = useState('')
  const [query, setQuery] = useState('')
  const [filter, setFilter] = useState<'ALL' | Complaint['status']>('ALL')
  const [view, setView] = useState<WorkspaceView>('overview')

  const request = useCallback(async (path: string, options: RequestInit = {}) => {
    const response = await fetch(`${API}${path}`, { ...options, headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}`, ...options.headers } })
    if (!response.ok) throw new Error((await response.text()) || `Request failed: ${response.status}`)
    return response.status === 204 ? null : response.json()
  }, [token])

  const loadData = useCallback(async () => {
    if (!token) return
    setLoading(true)
    try {
      const [complaintData, assignmentData, notificationData] = await Promise.all([request('/complaints'), request('/assignments'), request('/notifications')])
      setComplaints(complaintData); setAssignments(assignmentData); setNotifications(notificationData); setNotice('Live data refreshed')
    } catch (error) { setNotice(error instanceof Error ? error.message : 'Unable to load service data') } finally { setLoading(false) }
  }, [request, token])

  useEffect(() => { loadData() }, [loadData])

  const authenticate = async (event: FormEvent) => {
    event.preventDefault(); setAuthError('')
    try {
      if (authMode === 'signup') {
        const signup = await fetch(`${API}/auth/signup`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ email, password }) })
        if (!signup.ok) throw new Error(await signup.text() || 'Signup failed')
      }
      const response = await fetch(`${API}/auth/login`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ email, password }) })
      if (!response.ok) throw new Error('Invalid email or password')
      const data = await response.json(); localStorage.setItem('resolvenow-token', data.token); setToken(data.token)
    } catch (error) { setAuthError(error instanceof Error ? error.message : 'Authentication failed') }
  }

  const createComplaint = async (event: FormEvent) => {
    event.preventDefault()
    try { await request('/complaints', { method: 'POST', body: JSON.stringify(newComplaint) }); setNewComplaint({ title: '', description: '', customerEmail: '' }); setShowNew(false); await loadData() } catch (error) { setNotice(error instanceof Error ? error.message : 'Unable to create complaint') }
  }

  const updateStatus = async (id: number, status: Complaint['status']) => { try { await request(`/complaints/${id}/status?value=${status}`, { method: 'PATCH' }); await loadData() } catch (error) { setNotice(error instanceof Error ? error.message : 'Unable to update status') } }
  const logout = () => { localStorage.removeItem('resolvenow-token'); setToken(null) }

  if (!token) return <main className="auth-shell"><div className="auth-panel"><div className="brand-mark"><ShieldCheck size={20} /> RESOLVENOW</div><div className="auth-copy"><span className="eyebrow">OPERATIONS CONSOLE / 01</span><h1>Keep every case moving.</h1><p className="lede">A focused command desk for customer issues, ownership, and follow-through.</p></div><div className="auth-card"><div className="card-kicker"><span>{authMode === 'login' ? 'Welcome back' : 'Create access'}</span><span className="secure-label"><ShieldCheck size={13} /> Secure gateway</span></div><form onSubmit={authenticate} className="auth-form"><label>Email<input type="email" value={email} onChange={event => setEmail(event.target.value)} required placeholder="you@company.com" /></label><label>Password<input type="password" value={password} onChange={event => setPassword(event.target.value)} required minLength={8} placeholder="At least 8 characters" /></label>{authError && <p className="error">{authError}</p>}<button className="primary" type="submit">{authMode === 'login' ? 'Enter command desk' : 'Create operator account'} <ArrowRight size={17} /></button></form><button className="text-button" onClick={() => setAuthMode(authMode === 'login' ? 'signup' : 'login')}>{authMode === 'login' ? 'Need an account? Sign up' : 'Already registered? Sign in'}</button></div></div><div className="auth-aside"><span>RESOLVENOW / CASE INTELLIGENCE</span><strong>Resolve the signal.<br />Protect the relationship.</strong><div className="aside-rule" /><p>Every complaint has a next best action. Keep the queue visible, the handoffs accountable, and the customer in the loop.</p><div className="aside-stats"><div><strong>24/7</strong><small>case visibility</small></div><div><strong>01</strong><small>shared command desk</small></div></div></div></main>

  const counts = { open: complaints.filter(item => item.status === 'OPEN').length, active: complaints.filter(item => item.status === 'IN_PROGRESS').length, resolved: complaints.filter(item => item.status === 'RESOLVED' || item.status === 'CLOSED').length }
  const visibleComplaints = complaints.filter(item => filter === 'ALL' || item.status === filter).filter(item => `${item.title} ${item.description} ${item.customerEmail}`.toLowerCase().includes(query.toLowerCase()))
  const assignmentPage = <section className="full-page-panel"><div className="page-heading"><div><span className="eyebrow">OWNERSHIP / ASSIGNMENTS</span><h2>Assignment board</h2><p>See who owns each case and where the next handoff sits.</p></div><ClipboardList size={30} /></div>{assignments.length === 0 ? <div className="empty">No assignments recorded.</div> : <div className="assignment-grid">{assignments.map(item => <article className="assignment-card" key={item.id}><span>CASE #{item.complaintId}</span><strong>{item.department}</strong><small>{item.agent}</small><a href="#overview" onClick={() => setView('overview')}>Open complaint <ArrowRight size={14} /></a></article>)}</div>}</section>
  const activityPage = <section className="full-page-panel"><div className="page-heading"><div><span className="eyebrow">NETWORK / ACTIVITY</span><h2>Activity log</h2><p>Every assignment and status handoff, in one chronological stream.</p></div><Bell size={30} /></div>{notifications.length === 0 ? <div className="empty">No events recorded.</div> : <div className="activity-feed">{notifications.slice().reverse().map(item => <article className="feed-item" key={item.id}><span className="activity-dot" /><div><strong>{item.message}</strong><small>Case #{item.complaintId} · {item.recipient}</small></div><time>{new Date(item.sentAt).toLocaleDateString()}</time></article>)}</div>}</section>
  return <main className="app-shell"><header className="topbar"><div className="brand-mark"><ShieldCheck size={20} /> RESOLVENOW</div><nav className="main-nav" aria-label="Workspace views">{([['overview', 'Overview'], ['assignments', 'Assignments'], ['activity', 'Activity']] as const).map(([key, label]) => <button key={key} className={view === key ? 'active' : ''} onClick={() => setView(key)}>{label}</button>)}</nav><div className="topbar-meta"><span><Activity size={15} /> Gateway online</span><button className="icon-button" title="Refresh data" onClick={loadData}><RefreshCw size={17} className={loading ? 'spin' : ''} /></button><button className="profile" onClick={logout}><UserRound size={16} /> Operator <LogOut size={15} /></button></div></header><section className="workspace"><div className="intro"><div><span className="eyebrow">SERVICE CONTROL / TODAY</span><h1>{view === 'overview' ? <>Good work starts<br /><em>with visibility.</em></> : view === 'assignments' ? <>Keep ownership<br /><em>in motion.</em></> : <>Every handoff<br /><em>leaves a trace.</em></>}</h1></div>{view === 'overview' && <button className="primary add-button" onClick={() => setShowNew(true)}><CirclePlus size={18} /> New complaint</button>}</div><div className="metrics"><div><span>OPEN QUEUE</span><strong>{counts.open.toString().padStart(2, '0')}</strong><small>Needs triage</small></div><div><span>IN PROGRESS</span><strong>{counts.active.toString().padStart(2, '0')}</strong><small>Being handled</small></div><div><span>RESOLVED</span><strong>{counts.resolved.toString().padStart(2, '0')}</strong><small>Closed this cycle</small></div><div className="metric-accent"><span>EVENTS LOGGED</span><strong>{notifications.length.toString().padStart(2, '0')}</strong><small>Across the network</small></div></div>{view === 'overview' ? <div className="content-grid"><section className="queue"><div className="section-heading"><div><span className="eyebrow">CASE FLOW</span><h2>Complaint queue</h2></div><span className="record-count">{visibleComplaints.length} of {complaints.length} records</span></div><div className="queue-tools"><label className="search-field"><Search size={16} /><input value={query} onChange={event => setQuery(event.target.value)} placeholder="Search cases, customers, or descriptions" /></label><div className="filter-tabs">{(['ALL', 'OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED'] as const).map(item => <button key={item} className={filter === item ? 'active' : ''} onClick={() => setFilter(item)}>{item === 'ALL' ? 'All' : item.replace('_', ' ')}</button>)}</div></div>{visibleComplaints.length === 0 ? <div className="empty">No cases match this view.</div> : <div className="complaint-list">{visibleComplaints.map(complaint => <article className="complaint-row" key={complaint.id}><div className="case-id">#{String(complaint.id).padStart(3, '0')}</div><div className="case-copy"><h3>{complaint.title}</h3><p>{complaint.description}</p><small>{complaint.customerEmail}</small></div><div className="case-actions"><span className={`status ${complaint.status.toLowerCase()}`}>{complaint.status.replace('_', ' ')}</span><select value={complaint.status} onChange={event => updateStatus(complaint.id, event.target.value as Complaint['status'])} aria-label={`Update status for ${complaint.title}`}><option value="OPEN">Open</option><option value="IN_PROGRESS">In progress</option><option value="RESOLVED">Resolved</option><option value="CLOSED">Closed</option></select></div></article>)}</div>}</section></div> : view === 'assignments' ? assignmentPage : activityPage}{notice && <div className="toast" onClick={() => setNotice('')}>{notice}<X size={15} /></div>}</section>{showNew && <div className="modal-backdrop" onClick={() => setShowNew(false)}><form className="modal" onSubmit={createComplaint} onClick={event => event.stopPropagation()}><div className="modal-heading"><div><span className="eyebrow">CASE INTAKE</span><h2>Open a complaint</h2></div><button className="icon-button" type="button" onClick={() => setShowNew(false)}><X size={18} /></button></div><label>Title<input value={newComplaint.title} onChange={event => setNewComplaint({ ...newComplaint, title: event.target.value })} required maxLength={200} /></label><label>Description<textarea value={newComplaint.description} onChange={event => setNewComplaint({ ...newComplaint, description: event.target.value })} required maxLength={4000} rows={5} /></label><label>Customer email<input type="email" value={newComplaint.customerEmail} onChange={event => setNewComplaint({ ...newComplaint, customerEmail: event.target.value })} required /></label><button className="primary" type="submit">Create case <ArrowRight size={17} /></button></form></div>}</main>
}

export default App
