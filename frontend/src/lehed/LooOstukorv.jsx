import { useState, React, useEffect } from 'react';
import Menuu from '../komponendid/Menuu';
import ToodeKaart from '../komponendid/ToodeKaart';
import { postMarksonad } from '../teenused/api';
import coopLogo from '../staatiline/logod/coop.png';
import maximaLogo from '../staatiline/logod/maxima.png';
import selverLogo from '../staatiline/logod/selver.png';
import rimiLogo from '../staatiline/logod/rimi.png';
import prismaLogo from '../staatiline/logod/prisma.png';
import MarksonadeLisamine from '../komponendid/MarksonadeLisamine';

function LooOstukorv() {

    const [marksonad, setMarksonad] = useState({})
    const [uusMarksona, setUusMarksona] = useState('')
    const [uusSisalduvus, setUusSisalduvus] = useState("roheline")
    const [tooteKogus, setTooteKogus] = useState(1)
    const [tooted, setTooted] = useState([])
    const logod = {
        Prisma: prismaLogo,
        Selver: selverLogo,
        Maxima: maximaLogo,
        Coop: coopLogo,
        Rimi: rimiLogo
    }

    async function fetchTooted(ms) {
        const vastus = await postMarksonad(marksonad)

        if (vastus.ok) {
            setTooted(vastus.marksonad)
        }
    }

    useEffect(() => {
        fetchTooted(marksonad)
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
        nupp.textContent = "Sisaldab"
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

    console.log(tooted)

    return (
        <>
            <Menuu />
            <div id='sisu' className='hele'>
                <div id="marksonad-konteiner">
                    <MarksonadeLisamine
                        setUusMarksona={setUusMarksona}
                        muudaSisalduvust={muudaSisalduvust}
                        lisaMarksona={lisaMarksona}
                        marksonad={marksonad}
                        uusMarksona={uusMarksona}
                        eemaldaMarksona={eemaldaMarksona}
                    />
                    <div className="teksti-paar">
                        <span className="tume-tekst">Kogus</span>
                        <div>
                            <div>
                                <button className='nupp hele-tekst tume2' id='sisalduvus' onClick={() => setTooteKogus(tooteKogus > 0 ? tooteKogus - 1 : 0)}>-</button>
                                <input type="text" name='marksona' className='hele tume-tekst umar-nurk tume-piir' value={tooteKogus} onChange={e => setTooteKogus(parseInt(e.target.value))} />
                                <button className='nupp hele-tekst tume2' id='sisalduvus' onClick={() => setTooteKogus(tooteKogus + 1)}>+</button>
                            </div>
                            <button className='nupp hele-tekst tume' id='sisalduvus' onClick={() => lisaToode()}>Lisa toode</button>
                        </div>
                    </div>
                </div>
                <div id="tooted-list-konteiner">
                    <div className="teksti-paar">
                        <span className="tume-tekst">Leitud {tooted.length} toodet</span>
                        <div id="tooted-list-valimine" className="umar-nurk">
                            <div id="tooted-list">
                            {tooted.map(toode => {
                                console.log(toode); // Log the entire object
                                console.log(toode.toodePiltURL); // Log the specific property
                                return (
                                    <ToodeKaart
                                        key={toode.tooteNimi}
                                        tooteNimetus={toode.tooteNimi}
                                        tukiHind={toode.tooteTükihind}
                                        uhikuHind={toode.tooteÜhikuHind}
                                        uhik={toode.ühik}
                                        soodus={toode.kasonSoodus}
                                        poodPilt={logod[toode.pood]}
                                        toodeUrl={toode.toodePiltURL}
                                    />
                                );
                            })}
                            </div>
                        </div>
                    </div>
                </div>
                <div id="ostukorv-konteiner">

                </div>
            </div>
        </>
    )
}

export default LooOstukorv;