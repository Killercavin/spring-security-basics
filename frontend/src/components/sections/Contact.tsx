import { useState, type FormEvent } from 'react'
import { Send, CheckCircle2 } from 'lucide-react'

interface FormState {
  name:     string
  email:    string
  org:      string
  siteType: string
  message:  string
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

export default function Contact() {
  const [form,      setForm]      = useState<FormState>(initialForm)
  const [submitted, setSubmitted] = useState(false)
  const [loading,   setLoading]   = useState(false)

  const set = (field: keyof FormState) =>
    (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) =>
      setForm(f => ({ ...f, [field]: e.target.value }))

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setLoading(true)
    // Simulated — wire to your backend or Formspree later
    await new Promise(r => setTimeout(r, 1000))
    setLoading(false)
    setSubmitted(true)
  }

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

        <form onSubmit={handleSubmit} className="space-y-5">

          <div className="grid sm:grid-cols-2 gap-4">
            <Field label="Full Name" required>
              <input
                type="text"
                value={form.name}
                onChange={set('name')}
                placeholder="Your name..."
                required
              />
            </Field>
            <Field label="Email Address" required>
              <input
                type="email"
                value={form.email}
                onChange={set('email')}
                placeholder="name@exampe.com"
                required
              />
            </Field>
          </div>

          <Field label="Organization & Role">
            <input
              type="text"
              value={form.org}
              onChange={set('org')}
              placeholder="Operations Manager, Company Name"
            />
          </Field>

          <Field label="Type of Premises">
            <select value={form.siteType} onChange={set('siteType')}>
              <option value="">Select one</option>
              {siteTypes.map(t => (
                <option key={t} value={t}>{t}</option>
              ))}
            </select>
          </Field>

          <Field label="What matters most to you?">
            <textarea
              value={form.message}
              onChange={set('message')}
              placeholder="Tell me about your front desk flow, reporting needs, or anything specific you want to see..."
              rows={4}
            />
          </Field>

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
            {loading ? 'Sending...' : 'Send Request'}
            {!loading && <Send size={15} />}
          </button>

        </form>
      </div>
    </section>
  )
}

/* reusable field wrapper */
function Field({
  label,
  required,
  children,
}: {
  label:    string
  required?: boolean
  children: React.ReactNode
}) {
  return (
    <div className="flex flex-col gap-1.5 [&_input]:field [&_select]:field [&_textarea]:field">
      <label className="text-sm font-semibold text-surface">
        {label}{required && <span className="text-green-mid ml-0.5">*</span>}
      </label>
      {children}
    </div>
  )
}