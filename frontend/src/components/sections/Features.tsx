import { CheckCircle2, LogIn, Search, Download } from 'lucide-react'

/* ── mini visuals ── */
function DashboardVisual() {
  return (
    <div className="bg-green-pale border border-gray-200 rounded-2xl p-5 space-y-3">
      <div className="grid grid-cols-2 gap-2.5">
        {[
          { n: '14', l: 'On Premises', w: '70%' },
          { n: '31', l: 'Today Total', w: '88%' },
        ].map(s => (
          <div key={s.l} className="bg-white border border-gray-100 rounded-xl p-3.5">
            <p className="font-display font-extrabold text-2xl text-surface leading-none">{s.n}</p>
            <p className="text-xs text-muted mt-1">{s.l}</p>
            <div className="mt-2.5 h-1.5 bg-gray-100 rounded-full overflow-hidden">
              <div className="h-full bg-green-mid rounded-full" style={{ width: s.w }} />
            </div>
          </div>
        ))}
      </div>
      {[
        { name: 'Visitor F',  time: '09:15 AM', status: 'In',      statusClass: 'bg-green-light text-green-deep' },
        { name: 'Visitor G',    time: '08:32 AM', status: 'In',      statusClass: 'bg-green-light text-green-deep' },
        { name: 'Visitor H',  time: '07:50 AM', status: 'Overdue', statusClass: 'bg-amber-50 text-amber-700' },
      ].map(row => (
        <div
          key={row.name}
          className="bg-white border border-gray-100 rounded-lg px-3.5 py-2.5 flex items-center justify-between"
        >
          <div>
            <p className="text-sm font-semibold text-surface">{row.name}</p>
            <p className="text-xs text-muted">{row.time}</p>
          </div>
          <span className={`text-xs font-semibold px-2.5 py-1 rounded-full ${row.statusClass}`}>
            {row.status}
          </span>
        </div>
      ))}
    </div>
  )
}

function ReturningVisitorVisual() {
  return (
    <div className="bg-green-pale border border-gray-200 rounded-2xl p-5 space-y-3">
      <div className="bg-white border border-gray-100 rounded-lg px-3.5 py-2.5 flex items-center gap-2.5">
        <LogIn size={14} className="text-muted flex-shrink-0" />
        <span className="text-sm text-muted">
          +254 712 345 678
          <span className="inline-block w-0.5 h-3.5 bg-green-mid ml-0.5 animate-blink align-text-bottom" />
        </span>
      </div>
      <div className="bg-white border-2 border-green-mid rounded-xl p-4 shadow-[0_4px_16px_rgba(37,168,94,0.12)]">
        <p className="text-[10px] font-bold uppercase tracking-widest text-green-mid mb-2">
          Returning Visitor Found
        </p>
        <p className="font-display font-bold text-base text-surface mb-0.5">Visitor F</p>
        <p className="text-xs text-muted mb-3">Usual host: Host A. · IT Support · 8 previous visits</p>
        <div className="flex gap-2">
          <span className="text-[11px] bg-green-light text-green-deep font-semibold px-2.5 py-1 rounded-full">
            Details pre-filled
          </span>
          <span className="text-[11px] bg-green-light text-green-deep font-semibold px-2.5 py-1 rounded-full">
            Check in now
          </span>
        </div>
      </div>
    </div>
  )
}

function SearchVisual() {
  return (
    <div className="bg-green-pale border border-gray-200 rounded-2xl p-5 space-y-2.5">
      <div className="bg-white border border-gray-100 rounded-lg px-3.5 py-2.5 flex items-center gap-2">
        <Search size={14} className="text-muted" />
        <span className="text-sm text-muted">Visitor Name · Last 30 days · Checked Out</span>
      </div>
      {[
        { name: 'Visitor I',  date: 'Jun 28' },
        { name: 'Visitor J',   date: 'Jun 25' },
        { name: 'Visitor K',  date: 'Jun 20' },
      ].map((r, i) => (
        <div
          key={i}
          className="bg-white border border-gray-100 rounded-lg px-3.5 py-2.5 flex items-center justify-between"
        >
          <p className="text-sm font-semibold text-surface">{r.name}</p>
          <div className="flex items-center gap-2.5">
            <span className="text-xs text-muted">{r.date}</span>
            <span className="text-xs bg-gray-100 text-muted font-semibold px-2 py-0.5 rounded-full">Out</span>
          </div>
        </div>
      ))}
      <div className="flex items-center justify-between pt-1">
        <p className="text-xs text-muted">3 results found</p>
        <button className="text-xs font-semibold text-green-mid flex items-center gap-1 hover:text-green-deep transition-colors">
          <Download size={12} />
          Export CSV/PDF
        </button>
      </div>
    </div>
  )
}

/* ── feature rows ── */
const features = [
  {
    label:  'Real-Time Dashboard',
    title:  'Know who is on your premises, with time',
    desc:   'A live dashboard shows every visitor currently checked in, how long they have been on site, and flags anyone overdue all without refreshing or asking anyone.',
    points: [
      'Live visitor count, updated instantly on every check-in and check-out',
      'Overdue visitor alerts - automatically flagged after a configurable threshold'
    ],
    visual: <DashboardVisual />,
    reverse: false,
  },
  {
    label:  'Returning Visitor Recognition',
    title:  'Frequent visitors check in under 10 seconds',
    desc:   'When a phone number is entered, the system looks up the visitor profile and pre-fills all details. No form, no repetition, no wasted time at the front desk.',
    points: [
      'Phone-based profile lookup on every registration',
      'Auto-fills name, visitor type, usual host, and zone',
      'Profile stays separate from visit records, full history is never lost',
    ],
    visual: <ReturningVisitorVisual />,
    reverse: true,
  },
  {
    label:  'Search & History',
    title:  'Find any visitor, any visit, instantly',
    desc:   'Search the full visit history by name, phone number, host, zone, visitor type, or date range. Every result is paginated and exportable to CSV or PDF file formats.',
    points: [
      'Partial name match - no need to remember exact spelling',
      'Filter by date range, visitor type, zone, or current status',
      'Export filtered results to CSV or PDF file formats in one click for reports and audits',
    ],
    visual: <SearchVisual />,
    reverse: false,
  },
]

export default function Features() {
  return (
    <section className="py-24 px-6" id="features">
      <div className="max-w-6xl mx-auto">

        <p className="text-xs font-bold uppercase tracking-widest text-green-mid mb-3">
          What Gatelog Does
        </p>
        <h2 className="font-display text-3xl lg:text-4xl font-extrabold text-surface tracking-tight leading-tight mb-4 max-w-xl">
          Built around how a front desk actually works
        </h2>
        <p className="text-body text-base leading-relaxed max-w-lg mb-20">
          Every feature in Gatelog exists to solve a specific problem that paper has created.
        </p>

        <div className="flex flex-col gap-24">
          {features.map(({ label, title, desc, points, visual, reverse }) => (
            <div
              key={title}
              className={[
                'grid lg:grid-cols-2 gap-12 lg:gap-16 items-center',
              ].join(' ')}
            >
              {/* Text */}
              <div className={reverse ? 'lg:order-2' : ''}>
                <p className="text-xs font-bold uppercase tracking-widest text-green-mid mb-3">
                  {label}
                </p>
                <h3 className="font-display text-2xl lg:text-[1.65rem] font-extrabold text-surface tracking-tight leading-tight mb-3">
                  {title}
                </h3>
                <p className="text-body text-base leading-relaxed mb-5">{desc}</p>
                <ul className="flex flex-col gap-2.5">
                  {points.map(p => (
                    <li key={p} className="flex items-start gap-2.5 text-sm text-body">
                      <CheckCircle2 size={15} className="text-green-mid mt-0.5 flex-shrink-0" />
                      {p}
                    </li>
                  ))}
                </ul>
              </div>

              {/* Visual */}
              <div className={reverse ? 'lg:order-1' : ''}>{visual}</div>
            </div>
          ))}
        </div>

      </div>
    </section>
  )
}