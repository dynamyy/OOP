import { useState, React } from 'react'
import standardPilt from '../staatiline/standard/standard-toode.png';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faXmarkCircle } from '@fortawesome/free-regular-svg-icons'

function ToodeKaart(props) {

    return (
        <div>
            <div className="toode-kaart-konteiner umar-nurk">
                <div className="toode-kaart-pilt-konteiner">
                    <FontAwesomeIcon icon={faXmarkCircle} className='ikoon toode-kaart-ikoon' onClick={(e) => props.lisaEbasobivToode(e, props.toode)} />
                    <div>
                        <img src={props.toodeUrl ? props.toodeUrl : standardPilt} alt="pilt" className={props.toodeUrl ? "toode-pilt" : "toode-pilt-standard"} />
                    </div>
                    <span className="tume-tekst">{props.tooteNimetus}</span>
                </div>
                <div className="toode-kaart-hind-konteiner">
                    <img src={props.poodPilt} alt="logo" className="logo-pilt" />
                    <span className="tume-tekst">{parseFloat(props.tukiHind).toFixed(2)} €</span>
                    <span className="tume-tekst">{parseFloat(props.uhikuHind).toFixed(2)} €/{props.uhik}</span>
                </div>
            </div>
        </div>
    )
}

export default ToodeKaart;