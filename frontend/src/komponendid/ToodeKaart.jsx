import { useState, React } from 'react'
import standardPilt from '../staatiline/standard/standard-toode.png';

function ToodeKaart(props) {

    return (
        <div>
            <div className="toode-kaart-konteiner">
                <div>
                    <img src={props.toodeUrl ? props.toodeUrl : standardPilt} alt="pilt" className="toode-pilt" />
                    <span className="tume-tekst">{props.tooteNimetus}</span>
                    <img src={props.poodPilt} alt="logo" className="logo-pilt" />
                </div>
                <span className="tume-tekst">{parseFloat(props.tukiHind).toFixed(2)} €</span>
                <span className="tume-tekst">{parseFloat(props.uhikuHind).toFixed(2)} €/{props.uhik}</span>
            </div>
        </div>
    )
}

export default ToodeKaart;