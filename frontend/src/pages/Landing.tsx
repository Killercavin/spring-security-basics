import Navbar from '../components/layout/Navbar'
import Hero from '../components/sections/Hero'
import Ticker from '../components/common/Ticker'
import Problems from '../components/sections/Problems'
import Features from '../components/sections/Features'
import Sites from '../components/sections/Sites'
import About from '../components/sections/About'
import Contact from '../components/sections/Contact'
import Footer from '../components/layout/Footer'

export default function Landing() {
  return (
    <div className="min-h-screen bg-white font-sans antialiased">
      <Navbar />
      <main>
        <Hero />
        <Ticker />
        <Problems />
        <Features />
        <Sites />
        <About />
        <Contact />
      </main>
      <Footer />
    </div>
  )
}