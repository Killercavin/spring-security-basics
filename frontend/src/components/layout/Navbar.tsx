import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { Menu, X } from 'lucide-react'

const links = [
  { label: 'Features',   href: '#features' },
  { label: 'Who It\'s For', href: '#for-who' },
  { label: 'About',      href: '#about' },
]

export default function Navbar() {
  const [scrolled,     setScrolled]     = useState(false)
  const [menuOpen,     setMenuOpen]     = useState(false)

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 24)
    window.addEventListener('scroll', onScroll, { passive: true })
    return () => window.removeEventListener('scroll', onScroll)
  }, [])

  const handleNavClick = (href: string) => {
    setMenuOpen(false)
    const el = document.querySelector(href)
    if (el) el.scrollIntoView({ behavior: 'smooth' })
  }

  return (
    <header
      className={[
        'fixed top-0 inset-x-0 z-50 transition-all duration-300',
        scrolled
          ? 'bg-white/95 backdrop-blur-md shadow-[0_1px_12px_rgba(13,31,22,0.08)] border-b border-gray-100'
          : 'bg-white/80 backdrop-blur-sm',
      ].join(' ')}
    >
      <nav className="max-w-6xl mx-auto px-6 h-16 flex items-center justify-between">
        {/* Logo */}
        <Link
          to="/"
          className="font-display font-bold text-xl text-surface tracking-tight"
        >
          Gate<span className="text-green-mid">log</span>
        </Link>

        {/* Desktop nav */}
        <ul className="hidden md:flex items-center gap-8">
          {links.map(({ label, href }) => (
            <li key={href}>
              <button
                onClick={() => handleNavClick(href)}
                className="text-sm font-medium text-body hover:text-green-deep transition-colors duration-200"
              >
                {label}
              </button>
            </li>
          ))}
        </ul>

        {/* CTA */}
        <div className="hidden md:block">
          <button
            onClick={() => handleNavClick('#contact')}
            className="
              inline-flex items-center gap-2 px-5 py-2 rounded-lg
              bg-green-deep text-white text-sm font-semibold
              hover:bg-green-mid transition-colors duration-200
              focus-visible:outline focus-visible:outline-2 focus-visible:outline-green-mid
            "
          >
            Request Demo
          </button>
        </div>

        {/* Mobile hamburger */}
        <button
          className="md:hidden text-body p-1"
          onClick={() => setMenuOpen(o => !o)}
          aria-label="Toggle menu"
        >
          {menuOpen ? <X size={22} /> : <Menu size={22} />}
        </button>
      </nav>

      {/* Mobile menu */}
      {menuOpen && (
        <div className="md:hidden bg-white border-t border-gray-100 px-6 py-4 flex flex-col gap-4">
          {links.map(({ label, href }) => (
            <button
              key={href}
              onClick={() => handleNavClick(href)}
              className="text-sm font-medium text-body hover:text-green-deep text-left transition-colors"
            >
              {label}
            </button>
          ))}
          <button
            onClick={() => handleNavClick('#contact')}
            className="
              inline-flex items-center justify-center px-5 py-2.5 rounded-lg
              bg-green-deep text-white text-sm font-semibold
              hover:bg-green-mid transition-colors duration-200
            "
          >
            Request Demo
          </button>
        </div>
      )}
    </header>
  )
}