import { useState, type FormEvent } from 'react'
import { Send, CheckCircle2 } from 'lucide-react'

interface FormState {
  name: string
  email: string
  org: string
  siteType: string
  message: string
}

const initialForm: FormState = {
  name: '', email: '', org: '', siteType: '', message: '',
}

const siteTypes = [
  'Corporate Office',
  'Clinic / Hospital',
  'School / College',
  'Residential Estate',
  'Government Office',
  'Other',
]


const FORMSPREE_ENDPOINT = import.meta.env.VITE_FORMSPREE_ENDPOINT

export default function Contact() {
  const [form, setForm] = useState<FormState>(initialForm)
  const [submitted, setSubmitted] = useState(false)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const set = (field: keyof FormState) =>
    (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) =>
      setForm(f => ({ ...f, [field]: e.target.value }))

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setLoading(true)
    setError(null)

    try {
      const response = await fetch(FORMSPREE_ENDPOINT, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Accept: 'application/json',
        },
        body: JSON.stringify({
          name: form.name,
          email: form.email,
          organization: form.org,
          siteType: form.siteType,
          message: form.message,
          _subject: `Demo Request from ${form.name}`,
          _replyto: form.email,
          _gotcha: '',
        }),
      })

      if (response.ok) {
        setSubmitted(true)
        setForm(initialForm)
      } else {
        const data = await response.json()
        setError(data.error || 'Something went wrong. Please try again.')
      }
    } catch (err) {
      console.error('Form submission error:', err)
      setError('Network error. Please check your connection and try again.')
    } finally {
      setLoading(false)
    }
  }

  // ─── Success Page ──────────────────────────────────────────────

  if (submitted) {
    return (
      <section className="bg-green-pale py-24 px-6" id="contact">
        <div className="max-w-xl mx-auto text-center">
          <div className="w-14 h-14 bg-green-light rounded-2xl flex items-center justify-center mx-auto mb-5">
            <CheckCircle2 size={28} className="text-green-mid" />
          </div>
          <h2 className="font-display text-2xl font-extrabold text-surface mb-3">
            Request received
          </h2>
          <p className="text-body text-base leading-relaxed">
            Thank you. I'll follow up with demo access within soon as I get your message.
          </p>
          <button
            onClick={() => { setSubmitted(false); setForm(initialForm) }}
            className="mt-7 text-sm font-medium text-green-deep hover:underline"
          >
            Send another request
          </button>
        </div>
      </section>
    )
  }

  // ─── Contact Form ──────────────────────────────────────────────

  return (
    <section className="bg-green-pale py-24 px-6" id="contact">
      <div className="max-w-xl mx-auto">

        <p className="text-xs font-bold uppercase tracking-widest text-green-mid mb-3">
          Get In Touch
        </p>
        <h2 className="font-display text-3xl lg:text-4xl font-extrabold text-surface tracking-tight leading-tight mb-3">
          Interested in a demo?
        </h2>
        <p className="text-body text-base leading-relaxed mb-10">
          Fill in your details and I'll get back to you with access to the live system.
        </p>

        {/* Error Alert */}
        {error && (
          <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm flex items-start gap-3">
            <svg className="w-5 h-5 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-5">

          {/* ─── Honeypot: hidden _gotcha field ────────────────── */}
          <div className="hidden" aria-hidden="true">
            <label htmlFor="gotcha">If you are human, leave this field empty</label>
            <input
              type="text"
              id="gotcha"
              name="_gotcha"
              value=""
              onChange={() => { }} // No-op: bots will fill this
              tabIndex={-1}
              autoComplete="off"
            />
          </div>

          <div className="grid sm:grid-cols-2 gap-4">
            <div className="flex flex-col gap-1.5">
              <label htmlFor="contact-name" className="text-sm font-semibold text-surface">
                Full Name <span className="text-green-mid ml-0.5">*</span>
              </label>
              <input
                id="contact-name"
                type="text"
                value={form.name}
                onChange={set('name')}
                placeholder="Your name..."
                required
                className="w-full px-4 py-3 rounded-lg border border-gray-200 bg-white text-surface text-sm placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-green-mid focus:border-transparent transition-colors duration-200"
              />
            </div>

            <div className="flex flex-col gap-1.5">
              <label htmlFor="contact-email" className="text-sm font-semibold text-surface">
                Email Address <span className="text-green-mid ml-0.5">*</span>
              </label>
              <input
                id="contact-email"
                type="email"
                value={form.email}
                onChange={set('email')}
                placeholder="name@example.com"
                required
                className="w-full px-4 py-3 rounded-lg border border-gray-200 bg-white text-surface text-sm placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-green-mid focus:border-transparent transition-colors duration-200"
              />
            </div>
          </div>

          <div className="flex flex-col gap-1.5">
            <label htmlFor="contact-org" className="text-sm font-semibold text-surface">
              Organization & Role
            </label>
            <input
              id="contact-org"
              type="text"
              value={form.org}
              onChange={set('org')}
              placeholder="Operations Manager, Company Name"
              className="w-full px-4 py-3 rounded-lg border border-gray-200 bg-white text-surface text-sm placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-green-mid focus:border-transparent transition-colors duration-200"
            />
          </div>

          <div className="flex flex-col gap-1.5">
            <label htmlFor="contact-siteType" className="text-sm font-semibold text-surface">
              Type of Premises
            </label>
            <div className="relative">
              <select
                id="contact-siteType"
                value={form.siteType}
                onChange={set('siteType')}
                className="w-full px-4 py-3 rounded-lg border border-gray-200 bg-white text-surface text-sm appearance-none focus:outline-none focus:ring-2 focus:ring-green-mid focus:border-transparent transition-colors duration-200 pr-11"
                style={{
                  backgroundImage: `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='8' viewBox='0 0 12 8'%3E%3Cpath d='M1 1l5 5 5-5' stroke='%236B7280' stroke-width='1.5' fill='none' stroke-linecap='round'/%3E%3C/svg%3E")`,
                  backgroundRepeat: 'no-repeat',
                  backgroundPosition: 'right 1rem center',
                }}
              >
                <option value="">Select one</option>
                {siteTypes.map(t => (
                  <option key={t} value={t}>{t}</option>
                ))}
              </select>
            </div>
          </div>

          <div className="flex flex-col gap-1.5">
            <label htmlFor="contact-message" className="text-sm font-semibold text-surface">
              What matters most to you?
            </label>
            <textarea
              id="contact-message"
              value={form.message}
              onChange={set('message')}
              placeholder="Tell me about your front desk flow, reporting needs, or anything specific you want to see..."
              rows={4}
              className="w-full px-4 py-3 rounded-lg border border-gray-200 bg-white text-surface text-sm placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-green-mid focus:border-transparent transition-colors duration-200 resize-y min-h-[100px]"
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="
              inline-flex items-center gap-2 px-6 py-3 rounded-lg
              bg-green-deep text-white font-semibold text-sm
              hover:bg-green-mid hover:-translate-y-0.5
              disabled:opacity-60 disabled:cursor-not-allowed disabled:hover:translate-y-0
              transition-all duration-200
              focus-visible:outline focus-visible:outline-2 focus-visible:outline-green-mid
            "
          >
            {loading ? (
              <>
                <svg className="animate-spin h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                Sending...
              </>
            ) : (
              <>
                Send Request
                <Send size={15} />
              </>
            )}
          </button>

        </form>
      </div>
    </section>
  )
}