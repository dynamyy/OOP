import { useState, React } from 'react'

function ToodeKaart(props) {

    return (
        <div>
            <div className="toode-kaart-konteiner">
                <img src={props.poodPilt} alt="logo" className="logo-pilt" />
                <span className="tume-tekst">{props.tooteNimetus}</span>
                <span className="tume-tekst">{props.tukiHind} €</span>
                <span className="tume-tekst">{props.uhikuHind} €/{props.uhik}</span>
            </div>
        </div>
    )
}

export default ToodeKaart;