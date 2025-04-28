import { useState, React } from 'react'

function ToodeKaart(props) {

    return (
        <div clasName="toode-kaart-konteiner">
            <span>{props.tooteNimetus}</span>
            <span>{props.tukiHind}</span>
            <span>{props.uhikuHind}</span>
        </div>
    )
}

export default ToodeKaart;