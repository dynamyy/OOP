import React, { useState, useEffect } from 'react'
import Marksona from '../komponendid/Marksona';


function MarksonadeLisamine(props) {

    return (
        <div>
            <input 
                type="text" 
                name='marksona-nimi' 
                className='hele tume-tekst' 
                id='marksona-nimi-input'
                value={props.uusMarksonaNimi} 
                onChange={e => props.setUusMarksonaNimi(e.target.value)}
            />
            <div className="teksti-paar">
                <label htmlFor="marksona" className="tume-tekst">Sisesta toote otsimiseks märksõna:</label>
                <div className="tekst-nupp-konteiner">
                    <input 
                        type="text" 
                            name='marksona' 
                        className='hele tume-tekst' 
                        value={props.uusMarksona} 
                        onChange={e => props.setUusMarksona(e.target.value)}
                        placeholder='Märksõna'
                        onKeyDown={e => {
                            if (e.key === 'Enter') {
                                props.lisaMarksona(props.uusMarksona)
                            }
                        }}
                         />
                    <button className='nupp hele-tekst roheline' id='sisalduvus' onClick={() => props.muudaSisalduvust()}><span>Sisaldab</span></button>
                    <button className='nupp tume2 hele-tekst' onClick={() => props.lisaMarksona(props.uusMarksona)}><span>Lisa</span></button>
                </div>
            </div>
            <div className="teksti-paar">
                <span className="tume-tekst">Märksõnad</span>
                <div>
                    {Object.entries(props.marksonad).map(([ms, varv]) => (
                        <Marksona 
                            key={ms}
                            eemaldaMarksona={props.eemaldaMarksona}
                            marksona={ms}
                            varv={varv}
                        />
                    ))}
                </div>
            </div>
        </div>
    )
}

export default MarksonadeLisamine;