import React, { useState, useEffect } from 'react'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faXmarkCircle } from '@fortawesome/free-regular-svg-icons'


function OstukorvPoodToodeKaart(props) {

    return (
        <div className={'ostukorv-pood-toode-kaart umar-nurk tume-g2'}>
            <div>
                <div className="pilt-taust hele">
                    <img src={props.pilt} alt="toote pilt" className='ostukorv-toode-pilt' />
                </div>
                <div>
                    <span className="tekst-hele">- {props.kogus} {props.nimetus}</span>
                </div>
            </div>
            <div>
                <span>{props.hind} €/tk</span>
                <span>{(props.hind * props.kogus).toFixed(2)} €</span>
            </div>
        </div>
    )
}

export default OstukorvPoodToodeKaart;