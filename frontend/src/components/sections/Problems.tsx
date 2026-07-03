import { Search, Eye, PenLine, BarChart2, AlertTriangle, Timer } from 'lucide-react'

const problems = [
  { id: 1, icon: <Search size={18} className="text-green-deep" />, title: 'Limited search, limited history', desc: 'Finding a visitor from last month means flipping through pages. There is no way to query, filter, or export anything without the hurdle spending most of your time in the book figuring it out.' },
  { id: 2, icon: <Eye size={18} className="text-green-deep" />, title: 'Low real-time visibility', desc: 'A paper log can tell you who is still inside the premises with time but you have to search through the entries manually. There is no live count, no overdue alert, no reliable picture of your premises.' },
  { id: 3, icon: <PenLine size={18} className="text-green-deep" />, title: 'Weak accountability', desc: 'Handwritten entries can be illegible, incomplete, or fabricated. There is no way to verify what was written or when.' },
  { id: 4, icon: <BarChart2 size={18} className="text-green-deep" />, title: 'No reports, no patterns', desc: 'Generating a weekly summary requires manual counting through records while also recording which is not easy in the long run. Peak hours, frequent visitors, and department traffic stay invisible.' },
  { id: 5, icon: <AlertTriangle size={18} className="text-green-deep" />, title: 'Data loss risk', desc: 'One damaged logbook destroys months of records. No backup, no recovery, and no way to know what is missing.' },
  { id: 6, icon: <Timer size={18} className="text-green-deep" />, title: 'Slow front desk flow', desc: 'Every returning visitor fills the same form again. No recognition, no auto-fill, no time saved for the receptionist or the visitor.' },
]

export default function Problems() {
  return (
    <section className="bg-green-pale section-padding" id="problem">
      <div className="container-standard">
        <p className="section-eyebrow">The Problem</p>
        <h2 className="section-title">Paper logbooks have never been enough</h2>
        <p className="section-sub">Every organization using a paper register sits on a gap between who they think is in the building and who actually is.</p>

        <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {problems.map(({ id, icon, title, desc }) => (
            <div key={id} className="bg-white border border-gray-200 rounded-xl p-5 hover:shadow-[0_4px_20px_rgba(13,31,22,0.06)] hover:border-green-light transition-all duration-200">
              <div className="w-9 h-9 bg-green-light rounded-lg flex items-center justify-center mb-4">{icon}</div>
              <h3 className="font-display font-bold text-sm text-dark mb-1.5">{title}</h3>
              <p className="text-sm text-muted leading-relaxed">{desc}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}