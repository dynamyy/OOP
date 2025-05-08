import { useState, React } from 'react'
import Menuu from '../komponendid/Menuu'

function LooOstukorv(props) {

    return (
        <>
            <Menuu />
            <div id='sisu' className='hele'>
                <div key={props.voti} className="ostukorv-list-toode">
                    <span className="hele-tekst">- Kogus: {props.toode.kogus}</span>
                </div>
            </div>
        </>
    )
}

export default LooOstukorv;