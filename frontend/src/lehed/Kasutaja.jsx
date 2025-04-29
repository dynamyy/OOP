import React, { useEffect, useState } from 'react'
import Menuu from '../komponendid/Menuu'
import LoginAken from '../komponendid/LoginAken'
import Kliendikaart from '../komponendid/Kliendikaart'
import authTeenus from '../teenused/AuthTeenus'

function Kasutaja() {
    const [kliendikaartideMuutmine, setKliendikaartideMuutmine] = useState(false);
    const [onSisselogitud, setOnSisseLogitud] = useState(false);
    const [kasutaja, setKasutaja] = useState('');
    

    useEffect(() => {
        const checkSisselogitud = async () => {
            const isLoggedIn = await authTeenus.kasSisselogitud();
            setOnSisseLogitud(isLoggedIn);

            if (isLoggedIn) {
                const kasutajaNimi = await authTeenus.getKasutaja();
                setKasutaja(kasutajaNimi);
            }
        };

        checkSisselogitud();
    }, []);


    if (!onSisselogitud) {
        return <LoginAken />;
    }

    function toggleKliendikaardiMuutmine() {
        setKliendikaartideMuutmine(prev => !prev);
    }

    function valiKliendikaart(pood) {
        if (!kliendikaartideMuutmine) {
            return;
        }

        console.log(pood);
    }

    return (
        <>
            <Menuu />
            <div id="sisu" className="hele">
                <div className="tume umar-nurk hele-tekst" id="s-log">
                    <h2>Kasutaja {kasutaja}</h2>
                    
                    <h2>Kliendikaardid:</h2>
                    <div className="kliendikaardid samal-real">
                        <Kliendikaart poeNimi="COOP" varv={kliendikaartideMuutmine ? 'roheline' : 'roheline roheline-hover-disabled'} kaartValitud={() => valiKliendikaart("COOP")}/>
                        <Kliendikaart poeNimi="Prisma" varv={kliendikaartideMuutmine ? 'roheline' : 'roheline roheline-hover-disabled'} kaartValitud={() => valiKliendikaart("Prisma")}/>
                        <Kliendikaart poeNimi="Rimi" varv={kliendikaartideMuutmine ? 'roheline' : 'roheline roheline-hover-disabled'} kaartValitud={() => valiKliendikaart("Rimi")}/>
                        <Kliendikaart poeNimi="Maxima" varv={kliendikaartideMuutmine ? 'roheline' : 'roheline roheline-hover-disabled'} kaartValitud={() => valiKliendikaart("Maxima")}/>
                        <Kliendikaart poeNimi="Selver" varv={kliendikaartideMuutmine ? 'roheline' : 'roheline roheline-hover-disabled'} kaartValitud={() => valiKliendikaart("Selver")}/>
                    </div>
                    <button className='nupp tume2 hele-tekst' onClick={() => toggleKliendikaardiMuutmine()}><span>{kliendikaartideMuutmine ? 'Lõpeta muutmine' : 'Muuda kliendikaarte'}</span></button>

                    <div className="samal-real">
                        <button className='nupp tume2 hele-tekst' onClick={() => console.log("nupp")}><span>Muuda parooli</span></button>
                        <button className='nupp tume2 hele-tekst' onClick={() => console.log("nupp")}><span>Kustuta konto</span></button>
                    </div>
                </div>
            </div>
        </>
    )
}

export default Kasutaja;
