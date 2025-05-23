import React, { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import authTeenus from '../teenused/AuthTeenus'
import { useNavigate } from 'react-router-dom';
import MuraFilter from './MuraFilter';

function Menuu() {

    const [onSisselogitud, setOnSisseLogitud] = useState(localStorage.getItem('AuthToken') !== null);
    
    useEffect(() => {
        const checkSisselogitud = async () => {
            const isLoggedIn = await authTeenus.kasSisselogitud();
            setOnSisseLogitud(isLoggedIn);
        };

        checkSisselogitud();
    }, []);

    const logiValja = () => {
        localStorage.removeItem('AuthToken');
        
        setOnSisseLogitud(false);
        
        window.location.reload();
    };

    return (
        <header className="menuu tume">
            <MuraFilter />
            <nav>
                <ul>
                    <li><Link to="/kasutaja" className='hele-tekst'>Kasutaja</Link></li>
                    <li><Link to="/ostukorvid" className='hele-tekst'>Ostukorvid</Link></li>
                    <li><Link to="/kasutusjuhend" className='hele-tekst'>Kasutusjuhend</Link></li>
                    {onSisselogitud && (
                        <li><Link to="#" className='hele-tekst' onClick={logiValja}>Logi välja</Link></li>
                    )}
                </ul>
            </nav>
        </header>
    )
}

export default Menuu;