import {
  LogIn, LogOut, AlertTriangle, FileDown, UserPlus, RotateCcw,
} from 'lucide-react'

interface TickerItem {
  icon: React.ReactNode
  text: string
}

const events: TickerItem[] = [
  { icon: <LogIn size={13} />,       text: 'Visitor checked in · Visitor L. · Block A · 09:14 AM' },
  { icon: <RotateCcw size={13} />,   text: 'Returning visitor recognised · Visitor M. · IT Support · 8 previous visits' },
  { icon: <LogOut size={13} />,      text: 'Visitor checked out · Duration 1h 22m · Peter O.' },
  { icon: <AlertTriangle size={13} />, text: 'Overdue visitor flagged · 3 hours on premises · Auto-flagged by system' },
  { icon: <FileDown size={13} />,    text: 'CSV report exported · 47 records · Finance Manager' },
  { icon: <UserPlus size={13} />,    text: 'New staff account created · Visitor N. · Receptionist role' },
  { icon: <LogIn size={13} />,       text: 'Visitor checked in · Visitor O. · Maintenance · 10:05 AM' },
  { icon: <RotateCcw size={13} />,   text: 'Returning visitor recognised · Visitor P. · 3 previous visits · Pre-filled' },
]

// duplicate for seamless loop
const doubled = [...events, ...events]

export default function Ticker() {
  return (
    <div
      className="bg-surface py-3.5 overflow-hidden select-none"
      aria-hidden="true"
    >
      <div className="flex animate-ticker" style={{ width: 'max-content' }}>
        {doubled.map((item, i) => (
          <div
            key={i}
            className="flex items-center gap-2.5 text-white/60 text-xs font-medium px-6 whitespace-nowrap flex-shrink-0"
          >
            <span className="text-green-mid flex-shrink-0">{item.icon}</span>
            {item.text}
            <span className="w-1 h-1 rounded-full bg-white/20 ml-6 flex-shrink-0" />
          </div>
        ))}
      </div>
    </div>
  )
}