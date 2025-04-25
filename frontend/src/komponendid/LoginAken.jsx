import React, { useState } from 'react'
import Menuu from '../komponendid/Menuu'
import { useNavigate } from 'react-router-dom';
import { postSisseLogimine } from '../teenused/api'
import { postRegistreerimine } from '../teenused/api'

function loginAken() {
    const navigate = useNavigate();
    const [sisselogimiseTeade, setSisselogimiseTeade] = useState('');
    const [email, setEmail] = useState('')
    const[parool, setParool] = useState('')

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
        const vastus = await postRegistreerimine(email, parool);
        const vastuseSonum = await vastus.sonum;

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
                    <button className='nupp tume2 hele-tekst' onClick={() => logiSisse(email, parool)}><span>Logi sisse</span></button>
                    <button className='nupp tume2 hele-tekst' onClick={() => registreeri(email, parool)}><span>Registreeri</span></button>
                </div>
            </div>
        </>
    )
}

export default loginAken;