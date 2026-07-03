import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Landing from './pages/Landing'

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Landing />} />
        {/* auth and app routes come later */}
      </Routes>
    </BrowserRouter>
  )
}