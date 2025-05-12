import React, { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import authTeenus from '../teenused/AuthTeenus'
import { useNavigate } from 'react-router-dom';

function Menuu() {
    const navigate = useNavigate();
    const [onSisselogitud, setOnSisseLogitud] = useState(false);
    
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
            <nav>
                <ul>
                    <li><Link to="/kasutaja" className='hele-tekst'>Kasutaja</Link></li>
                    <li><Link to="/ostukorvid" className='hele-tekst'>Ostukorvid</Link></li>
                    <li><Link to="/tooted" className='hele-tekst'>Tooted</Link></li>
                    {onSisselogitud && (
                        <li><Link to="#" className='hele-tekst' onClick={logiValja}>Logi välja</Link></li>
                    )}
                </ul>
            </nav>
        </header>
    )
}

export default Menuu;