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
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCartShopping } from '@fortawesome/free-solid-svg-icons';
import { useNavigate } from 'react-router-dom';
import '../staatiline/UusOstukorv.css'
import MuraFilter from '../komponendid/MuraFilter';

function LooOstukorv() {

    const navigeeri = useNavigate();
    const [marksonad, setMarksonad] = useState({})
    const [uusMarksona, setUusMarksona] = useState('')
    const [uusSisalduvus, setUusSisalduvus] = useState("roheline")
    const [tooteKogus, setTooteKogus] = useState(1)
    const [tooted, setTooted] = useState([])
    const [filtreeritudTooted, setFiltreeritudTooted] = useState([])
    const [filtrid, setFiltrid] = useState(() => {
    const salvestatud = localStorage.getItem("Filtrid");
    return salvestatud ? JSON.parse(salvestatud) : [];
    });
    const [ostukorv, setOstukorv] = useState({})
    const [ebasobivadTooted, setEbasobivadTooted] = useState([])
    const [ostukorviNimi, setOstukorviNimi] = useState('')
    const [tooteidKokku, setTooteidKokku] = useState(0);
    const [uuteToodeteLaadimine, setUuteToodeteLaadimine] = useState(false);
    const [marksonaError, setMarksonaError] = useState(false)
    const [ostukorvNimiError, setOstukorvNimiError] = useState(false)
    const elmRef = useRef(null);
    const logod = {
        Prisma: prismaLogo,
        Selver: selverLogo,
        Maxima: maximaLogo,
        Coop: coopLogo,
        Rimi: rimiLogo
    }

    useEffect(() => {
        if (Object.keys(marksonad).length === 0) {
            const salvestatudMarksonad = localStorage.getItem("Marksonad");
            if (salvestatudMarksonad) {
                setMarksonad(JSON.parse(salvestatudMarksonad));
            }
        }

        if (Object.keys(ostukorv).length === 0) {
            const salvestatudKorv = localStorage.getItem("Ostukorv");
            if (salvestatudKorv) {
                setOstukorv(JSON.parse(salvestatudKorv));
            }
        }
        if (ebasobivadTooted.length === 0) {
            const salvestatudEbasobivadTooted = localStorage.getItem("EbasobivadTooted");
            if (salvestatudEbasobivadTooted) {
                setEbasobivadTooted(JSON.parse(salvestatudEbasobivadTooted));
            }
        }
    }, []);

    useEffect(() => {
        localStorage.setItem("Ostukorv", JSON.stringify(ostukorv));
    }, [ostukorv]);

    useEffect(() => {
        localStorage.setItem("Filtrid", JSON.stringify(filtrid));
        if (filtrid.length === 0) {
            setFiltreeritudTooted(tooted);
        } else {
            setFiltreeritudTooted(tooted.filter(toode => filtrid.includes(toode.pood)));
        }
    }, [filtrid, tooted]);

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
            const uuendatudMarksonad = {...marksonad, [marksona]: uusSisalduvus};
            setMarksonad(uuendatudMarksonad)
            localStorage.setItem("Marksonad", JSON.stringify(uuendatudMarksonad));
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
        localStorage.setItem("Marksonad", JSON.stringify(eemaldatud));
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

    useEffect(() => {
        document.getElementById("marksona-input").classList.remove("punane-serv")
        setMarksonaError(false)
    }, [marksonad])

    useEffect(() => {
        const elem = document.getElementById("ostukorv-nimi");
        if (elem) {
            elem.classList.remove("hele-serv");
        }
        setOstukorvNimiError(false);
    }, [ostukorviNimi]);

    function lisaOstukorvi(marksonad) {
        const votmed = Object.keys(marksonad)

        if (Object.keys(marksonad).length === 0) {
            document.getElementById("marksona-input").classList.add("punane-serv")
            setMarksonaError(true)
        }

        if (votmed.length > 0) {
            const voti = votmed.join("");
            let uusKorv;
            setTooted([]);
            if (voti in ostukorv) {
                const uusKogus = ostukorv[voti].tooteKogus + tooteKogus;
                uusKorv = {...ostukorv, [voti]: {...ostukorv[voti], "tooteKogus": uusKogus}}
                setOstukorv(uusKorv)
            }
            else {
                uusKorv = {...ostukorv,
                    [voti]: {
                        "marksonad": marksonad, 
                        "tooteKogus": tooteKogus, 
                        "ebasobivadTooted": ebasobivadTooted
                    }}
                setOstukorv(uusKorv)
            }
            localStorage.setItem("Ostukorv", JSON.stringify(uusKorv));
            setMarksonad({})
            localStorage.setItem("Marksonad", JSON.stringify({}));
            localStorage.setItem("EbasobivadTooted", JSON.stringify([]));
            setTooteKogus(1);
            setEbasobivadTooted([])
        }
    }

    function eemaldaOstukorvist(voti) {
        const uusOstukorv = {...ostukorv}
        delete uusOstukorv[voti];
        setOstukorv(uusOstukorv);
        localStorage.setItem("Ostukorv", JSON.stringify(uusOstukorv));
    }

    function muudaToode(toode) {
        setMarksonad(toode.marksonad)
        localStorage.setItem("Marksonad", JSON.stringify(toode.marksonad));
        localStorage.setItem("EbasobivadTooted", JSON.stringify(toode.ebasobivadTooted));
        setTooteKogus(toode.tooteKogus)
        setEbasobivadTooted(toode.ebasobivadTooted)
        eemaldaOstukorvist(Object.keys(toode.marksonad).join(""))
    }

    async function looOstukorv(nimi, tooted) {

        if (nimi === "") {
            document.getElementById("ostukorv-nimi").classList.add("hele-serv")
            setOstukorvNimiError(true)
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

        const vastus = await postOstukorv(nimi, vormindatudTooted, localStorage.getItem('AuthToken'));
        if (vastus.ok) {
            setOstukorv({});
            setMarksonad({});
            setTooted([]);
            localStorage.removeItem("Marksonad");
            localStorage.removeItem("Ostukorv");
            navigeeri(`/ostukorv/${vastus.id}`);
        } else {
            console.log("Ostukorvi loomine nurjus");
        }
    }

    function lisaEbasobivToode(e, toodeId) {
        e.stopPropagation();
        const nupp = e.currentTarget.closest(".toode-kaart-ikoon");
        nupp.classList.toggle("poora-45")
        const emaDiv = e.currentTarget.closest(".toode-kaart-konteiner");
        emaDiv.classList.toggle("ebasobiv-toode");
        
        setEbasobivadTooted(prev => {
            if (prev.includes(toodeId)) {
                return prev.filter(t => t !== toodeId);
            } else {
                return [...prev, toodeId];
            }
        })

        if (ebasobivadTooted.includes(toodeId)) {
            localStorage.setItem("EbasobivadTooted", JSON.stringify(ebasobivadTooted.filter(t => t !== toodeId)));
        } else {
            localStorage.setItem("EbasobivadTooted", JSON.stringify([...ebasobivadTooted, toodeId]));
        }
    }

    const dynamicScroll = () => {
        const elm = elmRef.current;
        if (!elm || uuteToodeteLaadimine || tooted.length === tooteidKokku) return;

        const scrollPohjas = elm.scrollTop + elm.clientHeight >= elm.scrollHeight - 10;
        if (scrollPohjas) {
            console.log("laen juurde");
            setUuteToodeteLaadimine(true);
            fetchTooted(tooted.length);
        }
    }

    function filtreeriTooted(pood) {
        setFiltrid(prev => {
            if (prev.includes(pood)) {
                return prev.filter(filtreeritud => filtreeritud !== pood);
            } else {
                return [...prev, pood];
            }
        });
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
                            marksonaError={marksonaError}
                        />
                        <div className="teksti-paar">
                            <span className="tume-tekst">Kogus</span>
                            <div>
                                <div>
                                    <button className='nupp hele-tekst tume2' id='sisalduvus' onClick={() => setTooteKogus(tooteKogus > 0 ? tooteKogus - 1 : 0)}>-</button>
                                    <input type="text" name='marksona' className='hele tume-tekst umar-nurk' value={tooteKogus} onChange={e => setTooteKogus(parseInt(e.target.value))} />
                                    <button className='nupp hele-tekst tume2' id='sisalduvus' onClick={() => setTooteKogus(tooteKogus + 1)}>+</button>
                                </div>
                                <button className='nupp hele-tekst tume2' id='sisalduvus' onClick={() => lisaOstukorvi(marksonad)}>Lisa toode</button>
                                {Object.keys(ostukorv).length > 0 ? 
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
                                : null}
                            </div>
                        </div>
                        {Object.keys(ostukorv).length > 0 ? 
                            <div className="ostukorv-konteiner suletud tume umar-nurk">
                                <MuraFilter />
                                <div>
                                    <div id='ostukorv-nimi-konteiner'>
                                        <input 
                                            type="text" 
                                            name='ostukorv-nimi' 
                                            className='hele-tekst' 
                                            id='ostukorv-nimi'
                                            value={ostukorviNimi} 
                                            placeholder='Uus ostukorv'
                                            onChange={e => setOstukorviNimi(e.target.value)}
                                        />
                                        {ostukorvNimiError ? <span className='error-tekst hele-tekst'>Lisa ostukorvile nimi</span> : null}
                                    </div>
                                </div>
                                <div className="ostukorv-list">
                                    {Object.entries(ostukorv).map(([voti, toode]) => (
                                        <OstukorviToodeKaart
                                            key={voti}
                                            voti={voti}
                                            toode={toode}
                                            eemaldaOstukorvist={eemaldaOstukorvist}
                                            muudaToode={muudaToode}
                                        />
                                    ))}
                                </div>
                                {Object.keys(ostukorv).length > 0 ? 
                                <button className='nupp hele-tekst tume2' onClick={() => looOstukorv(ostukorviNimi, ostukorv)}>Loo ostukorv</button> :
                                <span className="hele-tekst">Ostukorv on tühi</span>}
                            </div> : null
                        }
                    </div>
                    <div id="tooted-list-konteiner">
                        <div className="teksti-paar">
                            <div id='tooted-list-pais'>
                                <span className="tume-tekst">Valik tehakse järgmistest toodetest</span>
                                <span className='tume-tekst'>
                                    Tooteid {filtreeritudTooted.length} / {tooteidKokku}
                                </span>
                            </div>
                            <div id="tooted-list-valimine" className="umar-nurk tume">
                                <MuraFilter />
                                <div id='tooted-list-filter'>
                                    <button
                                        className={'filter-nupp umar-nurk' + (filtrid.includes("Coop") ? " tume-piir sinine" : " hele")}
                                        onClick={() => {filtreeriTooted("Coop")}}
                                    ><img src={logod.Coop} alt="Coop"/></button>
                                    <button
                                        className={'filter-nupp umar-nurk' + (filtrid.includes("Maxima") ? " tume-piir sinine" : " hele")}
                                        onClick={() => {filtreeriTooted("Maxima")}}
                                    ><img src={logod.Maxima} alt="Maxima"/></button>
                                    <button
                                        className={'filter-nupp umar-nurk' + (filtrid.includes("Selver") ? " tume-piir sinine" : " hele")}
                                        onClick={() => {filtreeriTooted("Selver")}}
                                    ><img src={logod.Selver} alt="Selver"/></button>
                                    <button
                                        className={'filter-nupp umar-nurk' + (filtrid.includes("Rimi") ? " tume-piir sinine" : " hele")}
                                        onClick={() => {filtreeriTooted("Rimi")}}
                                    ><img src={logod.Rimi} alt="Rimi"/></button>
                                    <button
                                        className={'filter-nupp umar-nurk' + (filtrid.includes("Prisma") ? " tume-piir sinine" : " hele")}
                                        onClick={() => {filtreeriTooted("Prisma")}}
                                    ><img src={logod.Prisma} alt="Prisma"/></button>
                                </div>
                                <div id="tooted-list" ref={elmRef} onScroll={dynamicScroll}>
                                {filtreeritudTooted.map(toode => (
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
                                        ebasobivadTooted={ebasobivadTooted}
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