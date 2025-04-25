import React, { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import authTeenus from '../teenused/AuthTeenus'
import { useNavigate } from 'react-router-dom';

function Menuu() {
    const navigate = useNavigate();
    const [onSisselogitud, setOnSisseLogitud] = useState(false);
    
    useEffect(() => {
        setOnSisseLogitud(authTeenus.kasSisselogitud());
    }, []);

    const logiValja = () => {
        localStorage.removeItem('AuthToken');
        
        setOnSisseLogitud(false);
        
        window.location.reload();
    };

    return (
        <header className="menuu tume">
            <nav>
                <ul>
                    <li><Link to="/kasutaja">Kasutaja</Link></li>
                    <li><Link to="/ostukorvid">Ostukorvid</Link></li>
                    <li><Link to="/tooted">Tooted</Link></li>
                    {onSisselogitud && (
                        <li><Link to="#" onClick={logiValja}>Logi välja</Link></li>
                    )}
                </ul>
            </nav>
        </header>
    )
}

export default Menuu;