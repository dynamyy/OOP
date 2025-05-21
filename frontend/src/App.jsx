import LooOstukorv from "./lehed/LooOstukorv"
import Ostukorvid from "./lehed/Ostukorvid"
import Kasutaja from "./lehed/Kasutaja"
import Kasutusjuhend from "./lehed/Kasutusjuhend"
import Toode from "./lehed/Toode"
import OstukorvTulemus from "./lehed/OstukorvTulemus"
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import './staatiline/App.css'
import './staatiline/Ava.css'
import './staatiline/Kasutaja.css'
import './staatiline/Ostukorv.css'
import './staatiline/Ostukorvid.css'
import './staatiline/Kasutusjuhend.css'


function App() {

  return (
    <>
      <BrowserRouter>
        <Routes>
          <Route path='/' element={<Kasutaja />} />
          <Route path="/ostukorvid" element={<Ostukorvid />} />
          <Route path="/ostukorvid/loo-ostukorv" element={<LooOstukorv />} />
          <Route path="/kasutusjuhend" element={<Kasutusjuhend />} />
          <Route path="/kasutaja" element={<Kasutaja />} />
          <Route path="/Toode/:id" element={<Toode />} />
          <Route path="/ostukorv/:id" element={<OstukorvTulemus />} />
        </Routes>
      </BrowserRouter>
    </>
  )
}

export default App
