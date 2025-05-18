import { useState, React, useEffect } from 'react'
import Menuu from '../komponendid/Menuu'
import OstukorvidKaart from '../komponendid/OstukorvidKaart';
import LoginAken from '../komponendid/LoginAken';
import MuraFilter from '../komponendid/MuraFilter';
import { getOstukorvNimed, kustutaOstukorv } from '../teenused/api';
import { height } from '@fortawesome/free-regular-svg-icons/faAddressBook';
import { useNavigate } from 'react-router-dom';


function Ostukorvid() {
    
    const navigeeri = useNavigate();

    const [ostukorvid, setOstukorvid] = useState([])
    
    async function getOstukorv() {
        const vastus = await getOstukorvNimed(localStorage.getItem('AuthToken'))

        if (vastus.status === 401) {
            navigeeri("/kasutaja");
        } else if (vastus.ok) {
            setOstukorvid(vastus.ostukorvid.ostukorvid);
        } else {
            console.log(vastus.sonum)
        }
    }

    async function kustutaOstukorvDb(id) {
        const vastus = await kustutaOstukorv(id, localStorage.getItem('AuthToken'))
        if (vastus.ok) {
            setOstukorvid(ostukorvid.filter(ostukorv => ostukorv.id !== id))
        }
        console.log(vastus.sonum)
    }
        
    useEffect(() => {
        getOstukorv();
    }, []);

    useEffect(() => {
        document.getElementById("ostukorvid-loetelu-konteiner").style.maxHeight = "60%"
    })

    return (
        <>
            <Menuu />
            <div id='sisu' className='hele'>
                <div id="ostukorvid-loetelu-konteiner" className=" tume umar-nurk">
                    <MuraFilter />
                    <div>
                        <span>Sinu ostukorvid</span>
                        <button className='nupp tume2 hele-tekst' onClick={() => {navigeeri("/ostukorvid/loo-ostukorv")}}><span>Loo ostukorv</span></button>
                    </div>
                    <div id='ostukorvid-loetelu'>
                        {ostukorvid.length > 0 ? ostukorvid.map(ostukorv => (
                            <OstukorvidKaart 
                                key={ostukorv.id}
                                nimi={ostukorv.nimi}
                                id={ostukorv.id}
                                kustutaOstukorv={kustutaOstukorvDb}
                            />
                        )) : <span>Sul pole ostukorve</span>}
                    </div>
                </div>
            </div>
        </>
    )
}

export default Ostukorvid;