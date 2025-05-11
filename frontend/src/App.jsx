import LooOstukorv from "./lehed/LooOstukorv"
import Kasutaja from "./lehed/Kasutaja"
import Tooted from "./lehed/Tooted"
import Toode from "./lehed/Toode"
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import './staatiline/App.css'
import './staatiline/Ava.css'
import './staatiline/Kasutaja.css'

function App() {

  return (
    <>
      <BrowserRouter>
        <Routes>
          <Route path='/' element={<Kasutaja />} />
          <Route path="/ostukorvid" element={<LooOstukorv />} />
          <Route path="/tooted" element={<Tooted />} />
          <Route path="/kasutaja" element={<Kasutaja />} />
          <Route path="/Toode/:id" element={<Toode />} />
        </Routes>
      </BrowserRouter>
    </>
  )
}

export default App
