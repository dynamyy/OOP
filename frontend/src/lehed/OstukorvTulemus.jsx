import { useState, useEffect, React, use } from 'react'
import { useParams } from 'react-router-dom';
import Menuu from '../komponendid/Menuu'
import { getOstukorvTulemus } from '../teenused/api';
import OstukorvPoodTulp from '../komponendid/OstukorvPoodTulp';
import coopLogo from '../staatiline/logod/coop.png';
import maximaLogo from '../staatiline/logod/maxima.png';
import selverLogo from '../staatiline/logod/selver.png';
import rimiLogo from '../staatiline/logod/rimi.png';
import prismaLogo from '../staatiline/logod/prisma.png';

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
                .map(toode => toode === null ? 0 : toode.tukiHind * 1)
                .reduce((a, b) => a + b, 0);
        });
        const suurim = Math.max(...Object.values(uuedTulbad));
        Object.keys(uuedTulbad).forEach(pood => {
            uuedTulbad[pood] = suurim === 0 ? 0 : uuedTulbad[pood] / suurim;
        });
        console.log(uuedTulbad)
        setTulbad(uuedTulbad)
    }, [ostukorv])

    return (
        <>
            <Menuu />
            <div id='sisu' className='hele'>
                <div id="ostukorv-tulemused-konteiner">
                    {(ostukorv.poed || []).map((pood) => (
                        <OstukorvPoodTulp key={pood.pood} pood={pood} logo={logod[pood.pood]} korgus={tulbad[pood.pood]} />
                    ))}
                </div>
            </div>
        </>
    )
}

export default OstukorvTulemus;