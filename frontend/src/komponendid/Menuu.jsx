import React from 'react'
import { Link } from 'react-router-dom'

function Menuu() {
    return (
        <header className="menuu tume">
            <nav>
                <ul>
                    <li><Link to="/">Kasutaja</Link></li>
                    <li><Link to="/ostukorvid">Ostukorvid</Link></li>
                    <li><Link to="/tooted">Tooted</Link></li>
                </ul>
            </nav>
        </header>
    )
}

export default Menuu;