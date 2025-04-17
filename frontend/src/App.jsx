import Ava from "./lehed/Ava"
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import './staatiline/App.css'
import './staatiline/Ava.css'


function App() {

  return (
    <>
      <BrowserRouter>
        <Routes>
          <Route path='/' element={<Ava />} />
        </Routes>
      </BrowserRouter>
    </>
  )
}

export default App
