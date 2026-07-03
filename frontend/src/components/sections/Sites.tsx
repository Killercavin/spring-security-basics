import { Building2, Home, Hospital } from 'lucide-react'
import { BiSolidInstitution } from 'react-icons/bi'

const sites = [
  { id: 1, icon: <Building2 size={24} className="text-green-deep" />, name: 'Corporate Offices', desc: 'Track clients, vendors, and contractors across departments with full audit trails and role-based access.' },
  { id: 2, icon: <Hospital size={24} className="text-green-deep" />, name: 'Clinics & Hospitals', desc: 'Log visitors by ward, manage accompanying persons, and maintain visit records with timestamps.' },
  { id: 3, icon: <BiSolidInstitution size={24} className="text-green-deep" />, name: 'Institutions', desc: 'Know who is on campus at any moment. Log everybody, contractors, and inspection teams separately.' },
  { id: 4, icon: <Home size={24} className="text-green-deep" />, name: 'Residential Estates', desc: 'Track guests, deliveries, and service personnel through a single managed entry point.' },
]

export default function Sites() {
  return (
    <section className="bg-green-pale section-padding" id="for-who">
      <div className="container-standard">
        <p className="section-eyebrow">Who It's For</p>
        <h2 className="section-title">One system, every kind of premises</h2>
        <p className="section-sub">Gatelog is built to be site-agnostic. Whether you call your divisions departments, wards, blocks, or wings the system adapts to your language and flow.</p>

        <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-4">
          {sites.map(({ id, icon, name, desc }) => (
            <div key={id} className="bg-white border border-gray-200 rounded-xl p-5 hover:border-green-mid hover:shadow-[0_4px_20px_rgba(27,127,74,0.08)] transition-all duration-200 group">
              <div className="w-11 h-11 bg-green-light rounded-xl flex items-center justify-center mb-4 group-hover:bg-green-mid/10 transition-colors">{icon}</div>
              <h3 className="font-display font-bold text-sm text-dark mb-2">{name}</h3>
              <p className="text-sm text-muted leading-relaxed">{desc}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}