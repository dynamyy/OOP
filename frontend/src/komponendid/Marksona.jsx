import React, { useState, useEffect } from 'react'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faXmarkCircle } from '@fortawesome/free-regular-svg-icons'


function Marksona(props) {

    return (
        <div className={`marksona-konteiner umar-nurk2 tume2 ${props.varv}`}>
            <span className='hele-tekst'>{props.marksona}</span>
            <FontAwesomeIcon icon={faXmarkCircle} className='ikoon hele-tekst`' onClick={() => props.eemaldaMarksona(props.marksona)} />
        </div>
    )
}

export default Marksona;