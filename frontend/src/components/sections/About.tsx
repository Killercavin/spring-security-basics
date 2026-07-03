export default function About() {
  return (
    <section className="py-5 px-6" id="about">
      <div className="max-w-3xl mx-auto">

        <p className="text-xs font-bold uppercase tracking-widest text-green-mid mb-3">
          About Gatelog
        </p>
        <h2 className="font-display text-3xl lg:text-4xl font-extrabold text-surface tracking-tight leading-tight mb-10">
          Built with simplicity in mind
        </h2>

        <div className="space-y-5 text-body text-base leading-relaxed">
          <p>
            Gatelog started from a simple observation: almost every office, clinic,
            school, and gated community manages visitors with a paper logbook and
            almost all of them have the same problems with it. Limited way to search, low
            real-time visibility, manual audit trail that holds up.
          </p>
          <p>
            <strong className="text-surface font-semibold">
              The enterprise systems that solve this properly cost too much and do too
              much
            </strong>{' '}
            and they are built for multinational campuses, not small to medium institutions. Gatelog is built for those places. Fast enough for
            a busy front desk. Honest about what it does and what it does not do yet.
          </p>
        </div>

      </div>
    </section>
  )
}