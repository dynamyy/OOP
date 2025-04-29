import React, { useState } from 'react'
import Menuu from '../komponendid/Menuu'
import Kliendikaart from '../komponendid/Kliendikaart'
import { useNavigate } from 'react-router-dom';
import { postSisseLogimine } from '../teenused/api'
import { postRegistreerimine } from '../teenused/api'

function loginAken() {
    const navigate = useNavigate();
    const [sisselogimiseTeade, setSisselogimiseTeade] = useState('');
    const [email, setEmail] = useState('')
    const[parool, setParool] = useState('')
    const [kasutajaRegistreerimine, setKasutajaRegistreerimine] = useState(false);
    const [valitudPoed, setValitudPoed] = useState(new Set());

    async function logiSisse(email, parool) {
        const vastus = await postSisseLogimine(email, parool);

        if (!vastus.ok) {
            setSisselogimiseTeade(vastus.sonum);
        } else {
            localStorage.setItem('AuthToken', vastus.token);
            navigate('/ostukorvid');
        }
    }

    async function registreeri(email, parool) {
        const vastus = await postRegistreerimine(email, parool, Array.from(valitudPoed));
        const vastuseSonum = vastus.sonum;

        if (!vastus.ok) {
            if (vastuseSonum === "Parool ei vasta nõuetele") {
                setSisselogimiseTeade(vastus.sonum +
                    "\n- Peab olema vähemalt 8 tähemärki pikk" +
                    "\n- Sisaldama vähemalt ühte suurtähte" +
                    "\n- Sisaldama vähemalt ühte väiketähte" +
                    "\n- Sisaldama vähemalt ühte numbrit");
            } else {
                setSisselogimiseTeade(vastus.sonum);
            }
        } else {
            setSisselogimiseTeade(vastus.sonum);
            setEmail('');
            setParool('');
        }
    }

    function valiKliendikaart(pood) {
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

    return (
        <>
            <Menuu />
            <div id='sisu' className='hele'>
                <div className="tume umar-nurk hele-tekst" id='s-log'>
                    { sisselogimiseTeade && (
                    <div className="teade">
                    {sisselogimiseTeade.split('\n').map((line, i) => (
                        <p key={i}>{line}</p>
                    ))}
                    </div>
                    )}
                    <div className="teksti-paar">
                        <label htmlFor="email">Email</label>
                        <input type="text" name='email' className='hele tume-tekst' value={email} onChange={e => setEmail(e.target.value)} />
                    </div>
                    <div className="teksti-paar">
                        <label htmlFor="parool">Parool</label>
                        <input type="text" name='parool' className='hele tume-tekst' value={parool} onChange={e => setParool(e.target.value)} />
                    </div>

                    { !kasutajaRegistreerimine ?  
                        (
                            <>
                            <button className='nupp tume2 hele-tekst' onClick={() => logiSisse(email, parool)}><span>Logi sisse</span></button>
                            <button className='nupp tume2 hele-tekst' onClick={() => setKasutajaRegistreerimine(true)}><span>Registreeri uus kasutaja</span></button>
                            </>
                        ) : (
                            <>
                            <div className="teksti-paar">
                                <label>Vali olemasolevad poodide kliendikaardid</label>
                                <div className="kliendikaardid samal-real">
                                    <Kliendikaart poeNimi="COOP" varv={valitudPoed.has("COOP") ? "roheline" : "punane"} kaartValitud={() => valiKliendikaart("COOP")}/>
                                    <Kliendikaart poeNimi="Prisma" varv={valitudPoed.has("Prisma") ? "roheline" : "punane"} kaartValitud={() => valiKliendikaart("Prisma")}/>
                                    <Kliendikaart poeNimi="Rimi" varv={valitudPoed.has("Rimi") ? "roheline" : "punane"} kaartValitud={() => valiKliendikaart("Rimi")}/>
                                    <Kliendikaart poeNimi="Maxima" varv={valitudPoed.has("Maxima") ? "roheline" : "punane"} kaartValitud={() => valiKliendikaart("Maxima")}/>
                                    <Kliendikaart poeNimi="Selver" varv={valitudPoed.has("Selver") ? "roheline" : "punane"} kaartValitud={() => valiKliendikaart("Selver")}/>
                                </div>
                            </div>

                            <button className='nupp tume2 hele-tekst' onClick={() => registreeri(email, parool)}><span>Registreeri</span></button>
                            <button className='nupp tume2 hele-tekst' onClick={() => {
                                setKasutajaRegistreerimine(false);
                                setValitudPoed(new Set());
                                }}><span>Logi sisse olemasolevasse kasutajasse</span></button>
                            </>
                        )
                    }
                    
                
                
                </div>
            </div>
        </>
    )
}

export default loginAken;