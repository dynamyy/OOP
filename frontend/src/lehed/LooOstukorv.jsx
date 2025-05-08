import { useState, React, useEffect, use } from 'react';
import Menuu from '../komponendid/Menuu';
import ToodeKaart from '../komponendid/ToodeKaart';
import { postMarksonad, postOstukorv } from '../teenused/api';
import coopLogo from '../staatiline/logod/coop.png';
import maximaLogo from '../staatiline/logod/maxima.png';
import selverLogo from '../staatiline/logod/selver.png';
import rimiLogo from '../staatiline/logod/rimi.png';
import prismaLogo from '../staatiline/logod/prisma.png';
import MarksonadeLisamine from '../komponendid/MarksonadeLisamine';
import OstukorviToodeKaart from '../komponendid/OstukorviToodeKaart';

function LooOstukorv() {

    const [marksonad, setMarksonad] = useState({})
    const [uusMarksona, setUusMarksona] = useState('')
    const [uusSisalduvus, setUusSisalduvus] = useState("roheline")
    const [tooteKogus, setTooteKogus] = useState(1)
    const [tooted, setTooted] = useState([])
    const [ostukorv, setOstukorv] = useState({})
    const [ebasobivadTooted, setEbasobivadTooted] = useState([])
    const logod = {
        Prisma: prismaLogo,
        Selver: selverLogo,
        Maxima: maximaLogo,
        Coop: coopLogo,
        Rimi: rimiLogo
    }

    async function fetchTooted() {

        const vastus = await postMarksonad(marksonad)

        if (vastus.ok) {
            setTooted(vastus.marksonad)
        } else {
            console.log("Märksõnade saatmine nurjus")
        }
    }

    useEffect(() => {
        if (Object.keys(marksonad).length > 0) {
            console.log("Märksõnad saadetud")
            fetchTooted(marksonad)
        }
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
        setTooted([])
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

    function lisaOstukorvi(marksonad) {
        const votmed = Object.keys(marksonad)

        if (votmed.length > 0) {
            const voti = votmed.join("");
            setTooted([]);
            if (voti in ostukorv) {
                const uusKogus = ostukorv[voti].kogus + tooteKogus;
                setOstukorv({...ostukorv, [voti]: {...ostukorv[voti], "kogus": uusKogus}})
            }
            else {
                setOstukorv({...ostukorv, 
                    [voti]: {
                        "marksonad": marksonad, 
                        "kogus": tooteKogus, 
                        "ebasobivadTooted": ebasobivadTooted
                    }})
            }
            setMarksonad({})
            setTooteKogus(1);
            setEbasobivadTooted([])
        }
    }

    function eemaldaOstukorvist(voti) {
        const uusOstukorv = {...ostukorv}
        console.log(voti)
        delete uusOstukorv[voti];
        setOstukorv(uusOstukorv);
    }

    function muudaToode(toode) {
        setMarksonad(toode.marksonad)
        setTooteKogus(toode.kogus)
        setEbasobivadTooted(toode.ebasobivadTooted)
        eemaldaOstukorvist(Object.keys(toode.marksonad).join(""))
    }

    function looOstukorv(ostukorv) {
        const vastus = postOstukorv(ostukorv);

        if (vastus.ok) {
            console.log("Ostukorv loodud")
        } else {
            console.log("Ostukorvi loomine nurjus")
        }
    }

    console.log(ostukorv)

    function lisaEbasobivToode(e, toode) {
        e.stopPropagation();
        const nupp = e.currentTarget.closest(".toode-kaart-ikoon");
        nupp.classList.toggle("poora-45")
        const emaDiv = e.currentTarget.closest(".toode-kaart-konteiner");
        emaDiv.classList.toggle("ebasobiv-toode");

        setEbasobivadTooted(prev => {
            if (prev.includes(toode)) {
                return prev.filter(t => t !== toode);
            } else {
                return [...prev, toode];
            }
        })
    }

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
                            <button className='nupp hele-tekst tume' id='sisalduvus' onClick={() => lisaOstukorvi(marksonad)}>Lisa toode</button>
                        </div>
                    </div>
                    <div className="ostukorv-konteiner-1 tume umar-nurk">
                        <span className="hele-tekst">Ostukorv</span>
                        <div className="ostukorv-list">
                            {Object.entries(ostukorv).map(([voti, toode]) => (
                                <OstukorviToodeKaart
                                    key={voti}
                                    voti={voti}
                                    toode={toode}
                                    kogus={toode.kogus}
                                    eemaldaOstukorvist={eemaldaOstukorvist}
                                    muudaToode={muudaToode}
                                />
                            ))}
                        </div>
                        <button className='nupp hele-tekst tume2' onClick={() => looOstukorv(ostukorv)}>Loo ostukorv</button>
                    </div>
                </div>
                <div id="tooted-list-konteiner">
                    <div className="teksti-paar">
                        <span className="tume-tekst">Leitud {tooted.length} toodet</span>
                        <div id="tooted-list-valimine" className="umar-nurk">
                            <div id="tooted-list">
                            {tooted.map(toode => (
                                <ToodeKaart
                                    key={toode.id}
                                    tooteNimetus={toode.tooteNimi}
                                    tukiHind={toode.tooteTükihind}
                                    uhikuHind={toode.tooteÜhikuHind}
                                    uhik={toode.ühik}
                                    soodus={toode.kasonSoodus}
                                    poodPilt={logod[toode.pood]}
                                    toodeUrl={toode.toodePiltURL}
                                    lisaEbasobivToode={lisaEbasobivToode}
                                    toode={toode.tooteNimi}
                                />
                            ))}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </>
    )
}

export default LooOstukorv;