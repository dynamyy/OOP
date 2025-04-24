import { useState, React } from 'react'
import Menuu from '../komponendid/Menuu'
import { postSisseLogimine } from '../teenused/api'
import { postRegistreerimine } from '../teenused/api'

function Ava() {

    const [email, setEmail] = useState('')
    const[parool, setParool] = useState('')

    async function logiSisse(email, parool) {
        await postSisseLogimine(email, parool)
    }

    async function registreeri(email, parool) {
        await postRegistreerimine(email, parool)
    }

    return (
        <>
            <Menuu />
            <div id='sisu' className='hele'>
                <div className="tume umar-nurk hele-tekst" id='s-log'>
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

export default Ava;