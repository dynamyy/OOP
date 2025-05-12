import { useState, useEffect, React, use } from 'react'
import { useParams } from 'react-router-dom';
import Menuu from '../komponendid/Menuu'
import { getOstukorvTulemus } from '../teenused/api';

function OstukorvTulemus() {
    const { id } = useParams();
    const [ostukorv, setOstukorv] = useState({});

    useEffect(() => {
        const getOstukorv = async () => {
            const vastus = await getOstukorvTulemus(id, localStorage.getItem('AuthToken'));
            if (vastus.ok) {
                setOstukorv(vastus.ostukorvAndmed);
                console.log(vastus);
            } else {
                console.log(vastus.sonum);
            }
        };

        getOstukorv();
    }, []);

    return (
        <>
            <Menuu />
            <div id='sisu' className='hele'>
                <span>{JSON.stringify(ostukorv)}</span>
            </div>
        </>
    )
}

export default OstukorvTulemus;