import { CheckCircle2 } from 'lucide-react'
import { useNavigate } from 'react-router-dom'

const visitors = [
  { id: 1, initials: 'VA', name: 'Visitor A', meta: 'Finance Dept · 08:32 AM', status: 'in' },
  { id: 2, initials: 'VB', name: 'Visitor B', meta: 'IT Support · 09:15 AM', status: 'in' },
  { id: 3, initials: 'VC', name: 'Visitor C', meta: 'Reception · 07:50 AM', status: 'out' },
  { id: 4, initials: 'VD', name: 'Visitor D', meta: 'Security · 10:05 AM', status: 'in' },
  { id: 5, initials: 'VE', name: 'Visitor E', meta: 'Non Revenue · 3:05 PM', status: 'in' },
]

const stats = [
  { id: 1, value: '24', label: 'Today' },
  { id: 2, value: '12', label: 'On Premises' },
  { id: 3, value: '2', label: 'Overdue' },
]

export default function Hero() {
  const navigate = useNavigate()

  const scrollTo = (id: string) => {
    document.querySelector(id)?.scrollIntoView({ behavior: 'smooth' })
  }

  return (
    <section className="section-padding-lg" id="hero">
      <div className="container-standard grid lg:grid-cols-2 gap-12 items-center min-h-[70vh]">

        {/* Left */}
        <div>
          <h1 className="font-display text-[clamp(2.2rem,4vw,3.2rem)] font-extrabold text-dark leading-[1.1] tracking-tight mb-4">
            Know exactly who walked through your{' '}
            <em className="not-italic text-green-mid">gate</em> today
          </h1>

          <p className="text-body text-lg leading-relaxed mb-8 max-w-lg">
            Gatelog replaces paper logbooks with a fast, accountable digital system.
            Every visit logged, every second timestamped and visible to the right
            people in real time.
          </p>

          <ul className="flex flex-col gap-2 mb-9">
            {[
              'Real-time dashboard that reveals exactly who is on premises',
              'Returning visitor auto-fill & check-in under 30 seconds',
            ].map((item, i) => (
              <li key={i} className="flex items-start gap-2.5 text-sm text-body">
                <CheckCircle2
                  size={16}
                  className="text-green-mid mt-0.5 flex-shrink-0"
                />
                {item}
              </li>
            ))}
          </ul>

          {/* Primary CTAs */}
          <div className="flex flex-wrap items-center gap-4">
            <button
              onClick={() => scrollTo('#contact')}
              className="inline-flex items-center gap-2 px-6 py-3 rounded-lg bg-green-deep text-white font-semibold text-sm hover:bg-green-mid hover:-translate-y-0.5 transition-all duration-200"
            >
              Request Demo
            </button>
            <button
              onClick={() => scrollTo('#features')}
              className="inline-flex items-center gap-2 px-6 py-3 rounded-lg bg-white border border-gray-200 text-green-deep font-semibold text-sm hover:-translate-y-0.5 transition-all duration-200"
            >
              See how it works
            </button>
          </div>

          {/* Warm invite — for people who already have access */}
          <p className="mt-5 text-xs text-muted">
            Already have access?{' '}
            <button
              onClick={() => navigate('/login')}
              className="text-green-deep font-semibold hover:text-green-mid transition-colors duration-150 underline underline-offset-2 decoration-green-light hover:decoration-green-mid"
            >
              Sign in to your account
            </button>
          </p>
        </div>

        {/* Right */}
        <div className="relative">
          <div className="absolute -top-4 -right-4 z-10 bg-green-deep text-white text-xs font-semibold flex items-center gap-2 px-3.5 py-2.5 rounded-xl shadow-[0_4px_20px_rgba(27,127,74,0.35)]">
            <span className="w-2 h-2 rounded-full bg-green-light animate-pulse-dot" />
            Live · 12 on premises
          </div>

          <div className="bg-white border border-gray-200 rounded-2xl p-5 shadow-[0_4px_32px_rgba(13,31,22,0.08)]">
            <div className="flex items-center justify-between mb-4 pb-4 border-b border-gray-100">
              <span className="font-display font-bold text-sm text-dark tracking-tight">
                Today's Visitors
              </span>
              <div className="flex items-center gap-1.5 text-xs font-semibold text-green-mid">
                <span className="w-1.5 h-1.5 rounded-full bg-green-mid animate-pulse-dot" />
                Live
              </div>
            </div>

            <div className="flex flex-col divide-y divide-gray-50">
              {visitors.map((v) => (
                <div key={v.id} className="flex items-center gap-3 py-2.5">
                  <div className="w-9 h-9 rounded-full bg-green-light flex-shrink-0 flex items-center justify-center font-display font-bold text-xs text-green-deep">
                    {v.initials}
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-semibold text-dark truncate">{v.name}</p>
                    <p className="text-xs text-muted">{v.meta}</p>
                  </div>
                  <span
                    className={`text-xs font-semibold px-2.5 py-1 rounded-full flex-shrink-0 ${
                      v.status === 'in'
                        ? 'bg-green-light text-green-deep'
                        : 'bg-gray-100 text-muted'
                    }`}
                  >
                    {v.status === 'in' ? 'Checked In' : 'Checked Out'}
                  </span>
                </div>
              ))}
            </div>

            <div className="grid grid-cols-3 gap-2.5 mt-4 pt-4 border-t border-gray-100">
              {stats.map((s) => (
                <div key={s.id} className="bg-green-pale rounded-xl px-3 py-3 text-center">
                  <p className="font-display font-extrabold text-2xl text-dark leading-none mb-1">
                    {s.value}
                  </p>
                  <p className="text-xs text-muted font-medium">{s.label}</p>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}