import React, { useEffect, useState } from 'react'
import Menuu from '../komponendid/Menuu'
import LoginAken from '../komponendid/LoginAken'
import Kliendikaart from '../komponendid/Kliendikaart'
import authTeenus from '../teenused/AuthTeenus'
import MuraFilter from '../komponendid/MuraFilter'

function Kasutaja() {
    const [kliendikaartideMuutmine, setKliendikaartideMuutmine] = useState(false);
    const [valitudPoed, setValitudPoed] = useState(new Set());
    const [onSisselogitud, setOnSisseLogitud] = useState(false);
    const [kasutaja, setKasutaja] = useState('');
    const [parooliMuutmine, setParooliMuutmine] = useState(false);
    const [kasutajaKustutamine, setKasutajaKustutamine] = useState(false);
    const [sisselogimiseTeade, setSisselogimiseTeade] = useState('');
    const [vanaParool, setVanaParool] = useState('');
    const[uusParool, setUusParool] = useState('');
    

    useEffect(() => {
        const checkSisselogitud = async () => {
            const isLoggedIn = await authTeenus.kasSisselogitud();
            setOnSisseLogitud(isLoggedIn);

            if (isLoggedIn) {
                const kasutajaNimi = await authTeenus.getKasutaja();
                setKasutaja(kasutajaNimi);
                const vastus = await authTeenus.getKliendikaardid();
                if (vastus.ok) {
                    setValitudPoed(vastus.kliendikaardid);
                } else {
                    setOnSisseLogitud(false);
                }
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

        return valitudPoed.has(pood) ? 'roheline vajutatav' : 'punane vajutatav';
    }

    async function uuendaParool() {
        const vastus = await authTeenus.setParool([vanaParool, uusParool]);
        if (!vastus.ok) {
            if (vastus.sonum === "Uus parool ei vasta nõuetele") {
                setSisselogimiseTeade(vastus.sonum +
                    "\n- Peab olema vähemalt 8 tähemärki pikk" +
                    "\n- Sisaldama vähemalt ühte suurtähte" +
                    "\n- Sisaldama vähemalt ühte väiketähte" +
                    "\n- Sisaldama vähemalt ühte numbrit");
            } else {
                setSisselogimiseTeade(vastus.sonum);
            }
        } else {
            setSisselogimiseTeade("Parool edukalt muudetud");
            setVanaParool('');
            setUusParool('');
        }
    }

    async function kustutaKasutaja() {
        const vastus = await authTeenus.kustutaKasutaja(vanaParool);
        if (!vastus.ok) {
            setSisselogimiseTeade(vastus.sonum);
        } else {
            setKasutajaKustutamine(false);
            localStorage.removeItem('AuthToken');
            setOnSisseLogitud(false);
        }
    }

    if (parooliMuutmine) {
        return (
        <>
            <Menuu />
            <div id="sisu" className="hele">
                <div className="tume umar-nurk hele-tekst" id="s-log">
                    <MuraFilter />
                    { sisselogimiseTeade && (
                    <div className="teade">
                    {sisselogimiseTeade.split('\n').map((line, i) => (
                        <p key={i}>{line}</p>
                    ))}
                    </div>
                    )}
                    <div className="teksti-paar">
                        <label>Vana parool</label>
                        <input type="password" name='vanaParool' className='hele tume-tekst' value={vanaParool} onChange={e => setVanaParool(e.target.value)} />
                    </div>
                    <div className="teksti-paar">
                        <label>Uus parool</label>
                        <input type="password" name='uusParool' className='hele tume-tekst' value={uusParool} onChange={e => setUusParool(e.target.value)} />
                    </div>
                    <button className='nupp tume2 hele-tekst' onClick={() => uuendaParool()}><span>Muuda parool</span></button>
                    <button className='nupp tume2 hele-tekst' onClick={() => {setParooliMuutmine(false); setVanaParool(""); setUusParool(""); setSisselogimiseTeade("");}}><span>Tagasi</span></button>
                </div>
            </div>
        </>
        )
    }

    if (kasutajaKustutamine) {
        return (
        <>
            <Menuu />
            <div id="sisu" className="hele">
                <div className="tume umar-nurk hele-tekst" id="s-log">
                    <MuraFilter />
                    { sisselogimiseTeade && (
                    <div className="teade">
                    {sisselogimiseTeade.split('\n').map((line, i) => (
                        <p key={i}>{line}</p>
                    ))}
                    </div>
                    )}
                    <div className="teksti-paar">
                        <label>Kas oled kindel, et soovid oma kasutaja kustutada?</label>
                        <label>Seda tegevust ei saa tagasi võtta.</label>
                        <label>Parool:</label>
                        <input type="password" name='vanaParool' className='hele tume-tekst' value={vanaParool} onChange={e => setVanaParool(e.target.value)} />
                    </div>
                    <button className='nupp punane hele-tekst' onClick={() => kustutaKasutaja()}><span>Kustuta kasutaja</span></button>
                    <button className='nupp tume2 hele-tekst' onClick={() => {setKasutajaKustutamine(false); setVanaParool(""); setSisselogimiseTeade("");}}><span>Tagasi</span></button>
                </div>
            </div>
        </>
        )
    }

    return (
        <>
            <Menuu />
            <div id="sisu" className="hele">
                <div className="tume umar-nurk hele-tekst" id="s-log">
                    <MuraFilter />
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
                        <button className='nupp tume2 hele-tekst' onClick={() => setParooliMuutmine(prev => !prev)}><span>Muuda parooli</span></button>
                        <button className='nupp tume2 hele-tekst' onClick={() => setKasutajaKustutamine(true)}><span>Kustuta konto</span></button>
                    </div>
                </div>
            </div>
        </>
    )
}

export default Kasutaja;
