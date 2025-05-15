import { useState, useEffect, React, use } from 'react'
import { useParams } from 'react-router-dom';
import Menuu from '../komponendid/Menuu'
import { getOstukorvTulemus, uuendaOstukorvi } from '../teenused/api';
import OstukorvPoodTulp from '../komponendid/OstukorvPoodTulp';
import coopLogo from '../staatiline/logod/coop.png';
import maximaLogo from '../staatiline/logod/maxima.png';
import selverLogo from '../staatiline/logod/selver.png';
import rimiLogo from '../staatiline/logod/rimi.png';
import prismaLogo from '../staatiline/logod/prisma.png';
import OstukorvPoodToodeKaart from '../komponendid/OstukorvPoodToodeKaart';
import MuraFilter from '../komponendid/MuraFilter';

function OstukorvTulemus() {
    const { id } = useParams();
    const [ostukorv, setOstukorv] = useState({});
    const logod = {
            Prisma: prismaLogo,
            Selver: selverLogo,
            Maxima: maximaLogo,
            Coop: coopLogo,
            Rimi: rimiLogo
        };
    const [tulbad, setTulbad] = useState({});
    const [aktiivnePood, setAktiivnePood] = useState({});


    useEffect(() => {
        const getOstukorv = async () => {
            const vastus = await getOstukorvTulemus(id, localStorage.getItem('AuthToken'));
            if (vastus.ok) {
                setOstukorv(vastus.ostukorvAndmed);
                console.log(vastus);
            } else {
                console.log(vastus.sonum);
            }
        };

        getOstukorv();
    }, []);

    useEffect(() => {
        if (Object.keys(ostukorv).length > 0) {
            setAktiivnePood(ostukorv.poed.find(poodObj => poodObj.pood === 'Coop'));
            setTimeout(() => {
                const kast = document.getElementById("ostukorv-pood-tooted");
                const kast2 = document.getElementById("ostukorv-tooted-sisemine");
                if (kast) kast.style.maxHeight = "55vh"
                if (kast2) kast2.style.overflowY = "auto"
            }, 200)
        }
    }, [ostukorv.poed])

    console.log(aktiivnePood)

    useEffect(() => {
        const uuedTulbad = {
            Prisma: 0,
            Selver: 0,
            Maxima: 0,
            Coop: 0,
            Rimi: 0
        };
        if (!ostukorv.poed) return;
        ostukorv.poed.forEach(poodObj => {
            uuedTulbad[poodObj.pood] = (poodObj.tooted || [])
                .map(toode => toode === null ? 0 : toode.tukiHind * toode.kogus)
                .reduce((a, b) => a + b, 0);
        });
        const suurim = Math.max(...Object.values(uuedTulbad));
        Object.keys(uuedTulbad).forEach(pood => {
            uuedTulbad[pood] = suurim === 0 ? 0 : uuedTulbad[pood] / suurim;
        });
        console.log(uuedTulbad)
        setTulbad(uuedTulbad)
    }, [ostukorv])

    function varskendaOstukorvi(id) {
        const getOstukorv = async () => {
                    const vastus = await getOstukorvTulemus(id, localStorage.getItem('AuthToken'));
                    if (vastus.ok) {
                        setOstukorv(vastus.ostukorvAndmed);
                        console.log(vastus);
                    } else {
                        console.log(vastus.sonum);
                    }
                };

        const uuendamine = async () => {
            const vastus = await uuendaOstukorvi(id, localStorage.getItem('AuthToken'));
            if (vastus.ok) {
                console.log(vastus.ok);
                getOstukorv();
            } else {
                console.log(vastus.sonum);
            }
        };

        uuendamine();
    }

    return (
        <>
            <Menuu />
            <div id='sisu' className='hele ostukorv-sisu'>
                <h1 className='tume-tekst ostukorv-pealkiri'>{ostukorv.nimi}</h1>
                <div id="ostukorv-tulemused-konteiner">
                    <div className="ostukorv-tulemus-diagramm">
                        <div id="ostukorv-tulbad-konteiner">
                            {(ostukorv.poed || []).map((pood) => (
                                <OstukorvPoodTulp 
                                    key={pood.pood} 
                                    pood={pood} 
                                    logo={logod[pood.pood]}
                                    setAktiivnePood={setAktiivnePood}
                                    korgus={tulbad[pood.pood]} 
                                />
                            ))}
                        </div>
                    </div>
                    <div className="ostukorv-tulemus-ostukorv">
                        <div className="ostukorv-pood-tooted tume hele-tekst umar-nurk"  id='ostukorv-pood-tooted'>
                            <MuraFilter />
                            <div>
                                <span>Odavaim ostukorv</span>
                                <img src={logod[aktiivnePood.pood]} alt={aktiivnePood.pood} className="logo-pilt-ostukorv" />
                            </div>
                            <div id='ostukorv-tooted-sisemine'>
                                {aktiivnePood && Array.isArray(aktiivnePood.tooted) && aktiivnePood.tooted.length > 0
                                    ? aktiivnePood.tooted.map(toode => (
                                        toode !== null ?
                                        <OstukorvPoodToodeKaart 
                                            key={toode.nimetus}
                                            kogus={toode.kogus}
                                            pilt={toode.piltURL}
                                            nimetus={toode.nimetus}
                                            hind={toode.tukiHind}
                                        /> : null
                                    ))
                                    : null
                                }
                                <div>
                                    {}
                                    <span>
                                        Kokku: {aktiivnePood && 
                                        Array.isArray(aktiivnePood.tooted) && 
                                        aktiivnePood.tooted.length > 0 ? 
                                        aktiivnePood.tooted.map(
                                            toode => toode === null ? 0 : 
                                            toode.tukiHind * toode.kogus).reduce((a, b) => a + b, 0).toFixed(2) : 0} €
                                    </span>
                                </div>
                            </div>
                        </div>
                        <div className="ostukorv-tulemused-nupud">
                            <button className='nupp hele-tekst tume2' id='varskenda-nupp' onClick={() => varskendaOstukorvi(id)}>Värskenda</button>
                            <button className='nupp hele-tekst tume2' id='muuda-nupp' onClick={() => muudaOstukorvi()}>Muuda</button>
                        </div>
                    </div>
                </div>
            </div>
        </>
    )
}

export default OstukorvTulemus;