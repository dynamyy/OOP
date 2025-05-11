import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom';
import Menuu from '../komponendid/Menuu'
import { getToode } from '../teenused/api';
import standardPilt from '../staatiline/standard/standard-toode.png';
import '../staatiline/Toode.css'

function Toode() {
    const { id } = useParams();
    const [tooteInfo, setTooteInfo] = useState(new Map());
    const [hinnaMuutmine, setHinnaMuutmine] = useState(false);
    const [hind, setHind] = useState("");
    const [uhikuHind, setUhikuHind] = useState("");

    useEffect(() => {
        const getTooteInfo = async () => {
            const vastus = await getToode(id);
            if (vastus.ok) {
                setTooteInfo(vastus.tooteAndmed);
                setHind(vastus.tooteAndmed.tooteTukihind);
                setUhikuHind(vastus.tooteAndmed.tooteUhikuHind);
                console.log(vastus.tooteAndmed);
            } else {
                console.log("Ei saanud toote andmeid");
            }
        };

        getTooteInfo();
        }, []);

    function formaadiAeg(dateString) {
        const date = new Date(dateString);
        const tunnid = String(date.getHours()).padStart(2, '0');
        const minutid = String(date.getMinutes()).padStart(2, '0');
        const paev = String(date.getDate()).padStart(2, '0');
        const kuu = String(date.getMonth() + 1).padStart(2, '0');
        const aasta = date.getFullYear();

        return `${tunnid}:${minutid} ${paev}.${kuu}.${aasta}`;
    }

    function muudaHindu() {
        const algneOlek = hinnaMuutmine;
        setHinnaMuutmine(prev => !prev);

        if (algneOlek) {
            console.log("uuenda hinnad backendis");
        }
    }

    const kontrolliHinnaInput = (e, setter) => {
        let sisend = e.target.value;

        sisend = sisend.replace(',', '.');

        if (/^\d*\.?\d{0,2}$/.test(sisend)) {
            setter(sisend);
        }
    };

    return (
        <>
            <Menuu />
            <div id="sisu" className="hele">
                <div id="toode-konteiner">
                    <span className="tume-tekst">{tooteInfo.tooteNimi}</span>
                    <div class="umar-nurk" id="toode-andmed-konteiner">
                        <span className="tume-tekst">Pood: {tooteInfo.pood}</span>
                        <span className="tume-tekst">Hind: {tooteInfo.tooteTukihind}€</span>
                        { hinnaMuutmine && (<input type="numeric" name='hind' className='hele tume-tekst' value={hind} onChange={e => kontrolliHinnaInput(e, setHind)} /> ) }
                        <span className="tume-tekst">Ühikuhind: {tooteInfo.tooteUhikuHind}€/{tooteInfo.uhik}</span>
                        { hinnaMuutmine && (<input type="text" name='uhikuHind' className='hele tume-tekst' value={uhikuHind} onChange={e => kontrolliHinnaInput(e, setUhikuHind)} /> ) }
                        <span className="tume-tekst">Viimati uuendatud: {tooteInfo.viimatiUuendatud ? formaadiAeg(tooteInfo.viimatiUuendatud) : "Aeg puudub"}</span>
                        <button className='nupp tume2 hele-tekst' onClick={() => muudaHindu()}><span>Muuda hindu</span></button>
                    </div>
                </div>
                <div id="pilt-konteiner">
                    <img src={tooteInfo.toodePiltURL ? tooteInfo.toodePiltURL : standardPilt} alt="pilt" className={tooteInfo.toodePiltURL ? "toode-pilt-suur" : "toode-pilt-standard-suur"} />
                </div>
            </div>
        </>
    )
}

export default Toode;
