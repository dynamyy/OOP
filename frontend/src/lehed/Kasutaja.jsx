import React, { useEffect, useState } from 'react'
import Menuu from '../komponendid/Menuu'
import LoginAken from '../komponendid/LoginAken'
import Kliendikaart from '../komponendid/Kliendikaart'
import authTeenus from '../teenused/AuthTeenus'

function Kasutaja() {
    const [kliendikaartideMuutmine, setKliendikaartideMuutmine] = useState(false);
    const [valitudPoed, setValitudPoed] = useState(new Set());
    const [onSisselogitud, setOnSisseLogitud] = useState(false);
    const [kasutaja, setKasutaja] = useState('');
    

    useEffect(() => {
        const checkSisselogitud = async () => {
            const isLoggedIn = await authTeenus.kasSisselogitud();
            setOnSisseLogitud(isLoggedIn);

            if (isLoggedIn) {
                const kasutajaNimi = await authTeenus.getKasutaja();
                setKasutaja(kasutajaNimi);
                const poed = await authTeenus.getKliendikaardid();
                setValitudPoed(poed);
            }
        };

        checkSisselogitud();
    }, []);


    if (!onSisselogitud) {
        return <LoginAken />;
    }

    function toggleKliendikaardiMuutmine() {
        const algneOlek = kliendikaartideMuutmine;
        setKliendikaartideMuutmine(prev => !prev);

        if (algneOlek) {
            authTeenus.setKliendikaardid(Array.from(valitudPoed));
        }
    }

    function valiKliendikaart(pood) {
        if (!kliendikaartideMuutmine) {
            return;
        }

        setValitudPoed((poed) => {
            const uuendaPoode = new Set(poed);
            if (uuendaPoode.has(pood)) {
                uuendaPoode.delete(pood);
            } else {
                uuendaPoode.add(pood);
            }

            return uuendaPoode;
        });
    }

    function kliendikaardiVarv(pood) {
        if (!kliendikaartideMuutmine) {
            return valitudPoed.has(pood) ? 'roheline roheline-hover-disabled' : 'punane punane-hover-disabled';
        }

        return valitudPoed.has(pood) ? 'roheline' : 'punane';
    }

    return (
        <>
            <Menuu />
            <div id="sisu" className="hele">
                <div className="tume umar-nurk hele-tekst" id="s-log">
                    <h2>Kasutaja {kasutaja}</h2>
                    
                    <h2>Kliendikaardid:</h2>
                    <div className="kliendikaardid samal-real">
                        <Kliendikaart poeNimi="COOP" varv={kliendikaardiVarv("COOP")} kaartValitud={() => valiKliendikaart("COOP")}/>
                        <Kliendikaart poeNimi="Prisma" varv={kliendikaardiVarv("Prisma")} kaartValitud={() => valiKliendikaart("Prisma")}/>
                        <Kliendikaart poeNimi="Rimi" varv={kliendikaardiVarv("Rimi")} kaartValitud={() => valiKliendikaart("Rimi")}/>
                        <Kliendikaart poeNimi="Maxima" varv={kliendikaardiVarv("Maxima")} kaartValitud={() => valiKliendikaart("Maxima")}/>
                        <Kliendikaart poeNimi="Selver" varv={kliendikaardiVarv("Selver")} kaartValitud={() => valiKliendikaart("Selver")}/>
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
