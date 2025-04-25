import Ava from "./lehed/Ava"
import LooOstukorv from "./lehed/LooOstukorv"
import Kasutaja from "./lehed/Kasutaja"
import Tooted from "./lehed/Tooted"
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import './staatiline/App.css'
import './staatiline/Ava.css'
import './staatiline/UusOStukorv.css'

function App() {

  return (
    <>
      <BrowserRouter>
        <Routes>
          <Route path='/' element={<Ava />} />
          <Route path="/ostukorvid" element={<LooOstukorv />} />
          <Route path="/tooted" element={<Tooted />} />
          <Route path="/kasutaja" element={<Kasutaja />} />
        </Routes>
      </BrowserRouter>
    </>
  )
}

export default App
