import { useState, React } from 'react'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faPenToSquare } from '@fortawesome/free-regular-svg-icons'

function OstukorviToodeKaart(props) {

    return (
        <div key={props.voti} className="ostukorv-list-toode">
            <span className="hele-tekst" onClick={() => props.eemaldaOstukorvist(props.voti)}>- {Object.keys(props.toode.marksonad).slice(0, 2).join(", ")} {props.toode.kogus}   </span>
            <FontAwesomeIcon icon={faPenToSquare} className="ostukorv-ikoon hele-tekst" onClick={() => props.muudaToode(props.toode)} />
        </div>
    )
}

export default OstukorviToodeKaart;