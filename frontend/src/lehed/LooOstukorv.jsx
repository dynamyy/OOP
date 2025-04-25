import { useState, React, useEffect } from 'react'
import Menuu from '../komponendid/Menuu'
import Marksona from '../komponendid/Marksona'

function LooOstukorv() {

    const [marksonad, setMarksonad] = useState({})
    const [uusMarksona, setUusMarksona] = useState('')
    const [uusSisalduvus, setUusSisalduvus] = useState("roheline")

    useEffect(() => {

    }, [marksonad])

    function lisaMarksona(marksona) {
        if (marksona !== "" && marksona.trim() !== "" && !(marksona in marksonad)) {
            setMarksonad(vanadMarksonad => ({...vanadMarksonad, [marksona]: uusSisalduvus}))
        }
        setUusMarksona('')
        setUusSisalduvus("roheline")
        const nupp = document.getElementById('sisalduvus')
        nupp.classList.remove("punane")
        nupp.classList.add("roheline")
    }

    function eemaldaMarksona(marksona) {
        const eemaldatud = Object.entries(marksonad)
        .filter(([ms, varv]) => ms !== marksona)
        .reduce((uus, [ms, varv]) => {
            uus[ms] = varv;
            return uus;
        }, {});
        setMarksonad(eemaldatud)
    }

    function muudaSisalduvust() {
        const nupp = document.getElementById('sisalduvus')
        if (nupp.classList.contains("roheline")) {
            nupp.classList.add("punane")
            nupp.classList.remove("roheline")
            nupp.firstChild.textContent = "Ei sisalda"
            setUusSisalduvus("punane")
        } else {
            nupp.classList.add("roheline")
            nupp.classList.remove("punane")
            nupp.firstChild.textContent = "Sisaldab"
            setUusSisalduvus("roheline")
        }
    }

    return (
        <>
            <Menuu />
            <div id='sisu' className='hele'>
                <div id="marksonad-konteiner">
                    <div className="teksti-paar">
                        <label htmlFor="marksona" className="tume-tekst">Sisesta toote otsimiseks märksõna:</label>
                        <div className="tekst-nupp-konteiner">
                            <input type="text" name='marksona' className='hele tume-tekst' value={uusMarksona} onChange={e => setUusMarksona(e.target.value)} />
                            <button className='nupp hele-tekst roheline     ' id='sisalduvus' onClick={() => muudaSisalduvust()}><span>Sisaldab</span></button>
                            <button className='nupp tume2 hele-tekst' onClick={() => lisaMarksona(uusMarksona)}><span>Lisa</span></button>
                        </div>
                    </div>
                    <div className="teksti-paar">
                        <span className="tume-tekst">Märksõnad</span>
                        <div>
                            {Object.entries(marksonad).map(([ms, varv]) => (
                                <Marksona 
                                    key={ms}
                                    eemaldaMarksona={eemaldaMarksona}
                                    marksona={ms}
                                    varv={varv}
                                />
                            ))}
                        </div>
                    </div>
                </div>
                <div id="tooted-list-konteiner">

                </div>
                <div id="ostukorv-konteiner">

                </div>
            </div>
        </>
    )
}

export default LooOstukorv;