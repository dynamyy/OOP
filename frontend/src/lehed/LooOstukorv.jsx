import { useState, React, useEffect, useRef } from 'react';
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
import { FontAwesomeIcon as Font, FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCartShopping } from '@fortawesome/free-solid-svg-icons';
import { faArrowDown } from '@fortawesome/free-solid-svg-icons';
import '../staatiline/UusOStukorv.css'

function LooOstukorv() {

    const [marksonad, setMarksonad] = useState({})
    const [uusMarksona, setUusMarksona] = useState('')
    const [uusSisalduvus, setUusSisalduvus] = useState("roheline")
    const [tooteKogus, setTooteKogus] = useState(1)
    const [tooted, setTooted] = useState([])
    const [ostukorv, setOstukorv] = useState({})
    const [ebasobivadTooted, setEbasobivadTooted] = useState([])
    const [ostukorviNimi, setOstukorviNimi] = useState('Ostukorv')
    const [tooteidKokku, setTooteidKokku] = useState(0);
    const [uuteToodeteLaadimine, setUuteToodeteLaadimine] = useState(false);
    const [uusMarksonaNimi, setUusMarksonaNimi] = useState("Uus märksoõna")
    const elmRef = useRef(null);
    const logod = {
        Prisma: prismaLogo,
        Selver: selverLogo,
        Maxima: maximaLogo,
        Coop: coopLogo,
        Rimi: rimiLogo
    }

    async function fetchTooted(nihe) {

        const vastus = await postMarksonad(marksonad, nihe, localStorage.getItem('AuthToken'));

        if (vastus.ok) {
            setTooted(prev => [...prev, ...vastus.kuvaTootedDTO.tooted]);
            if (nihe === 0) {
                setTooteidKokku(vastus.kuvaTootedDTO.tooteidKokku);
            }
        } else {
            console.log("Märksõnade saatmine nurjus");
        }

        setUuteToodeteLaadimine(false);
    }

    useEffect(() => {
        if (Object.keys(marksonad).length > 0) {
            console.log("Märksõnad saadetud");
            setTooted([]);
            setTooteidKokku(0);
            fetchTooted(0);
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
        setMarksonad(eemaldatud);
        setTooted([]);
        setTooteidKokku(0);
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
                const uusKogus = ostukorv[voti].tooteKogus + tooteKogus;
                setOstukorv({...ostukorv, [voti]: {...ostukorv[voti], "tooteKogus": uusKogus}})
            }
            else {
                setOstukorv({...ostukorv, 
                    [voti]: {
                        "marksonad": marksonad, 
                        "tooteKogus": tooteKogus, 
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
        delete uusOstukorv[voti];
        setOstukorv(uusOstukorv);
    }

    function muudaToode(toode) {
        setMarksonad(toode.marksonad)
        setTooteKogus(toode.tooteKogus)
        setEbasobivadTooted(toode.ebasobivadTooted)
        eemaldaOstukorvist(Object.keys(toode.marksonad).join(""))
    }

    function looOstukorv(nimi, tooted) {

        if (nimi === "" || Object.values(tooted).length === 0) {
            console.log("Ostukorvi loomine nurjus")
            return;
        }

        const vormindatudTooted = Object.values(tooted).map(toode => ({
            ...toode,
            marksonad: Object.entries(toode.marksonad).map(([marksona, valikuVarv]) => ({
                marksona,
                valikuVarv
            })),
            ebasobivadTooted: toode.ebasobivadTooted.map(id => ({
                id
            }))
        }))

        const vastus = postOstukorv(nimi, vormindatudTooted, localStorage.getItem('AuthToken'));

        if (vastus.ok) {
            console.log("Ostukorv loodud")
        } else {
            console.log("Ostukorvi loomine nurjus")
        }
    }

    function lisaEbasobivToode(e, toodeId) {
        e.stopPropagation();
        const nupp = e.currentTarget.closest(".toode-kaart-ikoon");
        nupp.classList.toggle("poora-45")
        const emaDiv = e.currentTarget.closest(".toode-kaart-konteiner");
        emaDiv.classList.toggle("ebasobiv-toode");

        console.log(toodeId)
        
        setEbasobivadTooted(prev => {
            if (prev.includes(toodeId)) {
                return prev.filter(t => t !== toodeId);
            } else {
                return [...prev, toodeId];
            }
        })
    }

    const dynamicScroll = () => {
        const elm = elmRef.current;
        if (!elm || uuteToodeteLaadimine || tooted.length === tooteidKokku) return;

        const scrollPohjas = elm.scrollTop + elm.clientHeight >= elm.scrollHeight - 10;
        if (scrollPohjas) {
            setUuteToodeteLaadimine(true);
            fetchTooted(tooted.length);
        }
    }

    return (
        <>
            <Menuu />
            <div id='sisu' className='hele'>
                <div id="ostukorv-loomine-sisu">
                    <div id="marksonad-konteiner">
                        <MarksonadeLisamine
                            setUusMarksona={setUusMarksona}
                            muudaSisalduvust={muudaSisalduvust}
                            lisaMarksona={lisaMarksona}
                            marksonad={marksonad}
                            uusMarksona={uusMarksona}
                            eemaldaMarksona={eemaldaMarksona}
                            uusMarksonaNimi={uusMarksonaNimi}
                            setUusMarksonaNimi={setUusMarksonaNimi}
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
                                <div className="ostukorv-ikoon2-konteiner tume hele-tekst" onClick={() => {
                                        const ostukorv = document.querySelector(".ostukorv-konteiner")
                                        ostukorv.classList.toggle("suletud")
                                        ostukorv.classList.add("ease")
                                        setTimeout(() => {
                                            ostukorv.classList.remove("ease")
                                        }, 500)
                                    }}>
                                    <FontAwesomeIcon icon={faCartShopping} className="ostukorv-ikoon2" />
                                </div>
                            </div>
                        </div>
                        <div className="ostukorv-konteiner suletud tume umar-nurk">
                            <div>
                                <span className="hele-tekst">Ostukorv</span>
                                <FontAwesomeIcon icon={faArrowDown} className="ostukorv-ikoon ikoon-suur hele-tekst" onClick={() => {
                                    const ostukorv = document.querySelector(".ostukorv-konteiner")
                                    ostukorv.classList.toggle("suletud")
                                    ostukorv.classList.add("ease")
                                    setTimeout(() => {
                                        ostukorv.classList.remove("ease")
                                    }, 500)
                                }}/>
                            </div>
                            <div className="ostukorv-list">
                                {Object.entries(ostukorv).map(([voti, toode]) => (
                                    <OstukorviToodeKaart
                                        key={voti}
                                        voti={voti}
                                        toode={toode}
                                        kogus={toode.tooteKogus}
                                        eemaldaOstukorvist={eemaldaOstukorvist}
                                        muudaToode={muudaToode}
                                    />
                                ))}
                            </div>
                            {Object.keys(ostukorv).length > 0 ? 
                            <button className='nupp hele-tekst tume2' onClick={() => looOstukorv(ostukorviNimi, ostukorv)}>Loo ostukorv</button> :
                            <span className="hele-tekst">Ostukorv on tühi</span>}
                        </div>
                    </div>
                    <div id="tooted-list-konteiner">
                        <div className="teksti-paar">
                            <span className="tume-tekst">Leitud {tooteidKokku} toodet     (kuvatud {tooted.length})</span>
                            <div id="tooted-list-valimine" className="umar-nurk">
                                <div id="tooted-list" ref={elmRef} onScroll={dynamicScroll}>
                                {tooted.map(toode => (
                                    <ToodeKaart
                                        key={toode.id}
                                        id={toode.id}
                                        tooteNimetus={toode.tooteNimi}
                                        tukiHind={toode.tooteTukihind}
                                        uhikuHind={toode.tooteUhikuHind}
                                        uhik={toode.uhik}
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
            </div>
        </>
    )
}

export default LooOstukorv;