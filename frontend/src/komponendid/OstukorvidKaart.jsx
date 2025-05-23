import React, { useState, useEffect } from 'react'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faTrashCan, faPenToSquare } from '@fortawesome/free-solid-svg-icons';
import { useNavigate } from 'react-router-dom';


function OstukorvidKaart(props) {

    const navigeeri = useNavigate();

    return (
        <div className="ostukorvid-kaart-konteiner tume-g2 umar-nurk" onClick={() => {navigeeri(`/ostukorv/${props.id}`)}}>
            <span>{props.nimi}</span>
            <div>
                <FontAwesomeIcon 
                    icon={faTrashCan} 
                    className='ostukorvid-ikoon'
                    onClick={(e) => {
                        e.stopPropagation();
                        props.kustutaOstukorv(props.id)
                    }}
                />
            </div>
        </div>
    )
}

export default OstukorvidKaart;