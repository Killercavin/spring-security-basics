import { FaInstagram, FaLinkedin, FaTwitter, FaWhatsapp } from "react-icons/fa"

const Footer = () => {
  return (
    <footer className="bg-[#0D1F16] py-12 px-8 ">
      <div className="max-w-[1100px] mx-auto">
        <div className="flex flex-wrap items-start justify-between gap-8 pb-8 border-b border-white/10">
          <div className="max-w-[280px]">
            <a href="/" className="font-display font-bold text-lg text-white tracking-[-0.02em]">
              Gate<span className="text-[#25A85E]">log</span>
            </a>
            <p className="text-sm text-white/40 mt-2 leading-relaxed">
              Digital visitor management for offices, clinics, schools, and estates.
            </p>

            <div className="my-2 flex flex-wrap gap-2">
              <a href="https://linkedin.com/in/devcavin"><FaLinkedin /></a>
              <a href="https://x.com/devcavin"><FaTwitter /></a>
              <a href="https://instagram.com/devcavin"><FaInstagram /></a>
              <a href="https://wa.me/devcavin"><FaWhatsapp /></a>
            </div>
            
          </div>

          <div>
            <h4 className="text-xs font-bold tracking-widest uppercase text-white/40 mb-4">Product</h4>
            <ul className="space-y-2">
              <li><a href="#features" className="text-sm text-white/60 hover:text-white transition-colors">Features</a></li>
              <li><a href="#for-who" className="text-sm text-white/60 hover:text-white transition-colors">Who It's For</a></li>
              <li><a href="#about" className="text-sm text-white/60 hover:text-white transition-colors">About</a></li>
              <li><a href="#contact" className="text-sm text-white/60 hover:text-white transition-colors">Request Demo</a></li>
            </ul>
          </div>

          <div>
            <h4 className="text-xs font-bold tracking-widest uppercase text-white/40 mb-4">Developer</h4>
            <ul className="space-y-2">
              <li><a href="#" className="text-sm text-white/60 hover:text-white transition-colors">Documentation</a></li>
            </ul>
          </div>
        </div>

        <div className="pt-6 flex flex-wrap items-center justify-between gap-4">
          <p className="text-xs text-white/30">
            © 2026 Gatelog · Built by <a href="https://devcavin.vercel.app" target="_blank" rel="noopener" className="text-white/50 hover:text-[#25A85E] transition-colors">Cavin</a>
          </p>
        </div>
      </div>
    </footer>
  )
}

export default Footer