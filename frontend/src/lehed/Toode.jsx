import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom';
import Menuu from '../komponendid/Menuu'
import authTeenus from '../teenused/AuthTeenus'
import { getToode, uuendaTooteHind } from '../teenused/api';
import standardPilt from '../staatiline/standard/standard-toode.png';
import '../staatiline/Toode.css'
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import MuraFilter from '../komponendid/MuraFilter';

function Toode() {
    const { id } = useParams();
    const [tooteInfo, setTooteInfo] = useState(new Map());
    const [hinnaMuutmine, setHinnaMuutmine] = useState(false);
    const [hinnaMuutmineNupusilt, setHinnaMuutmineNupusilt] = useState("Muuda hindu");
    const [hind, setHind] = useState("");
    const [uhikuHind, setUhikuHind] = useState("");
    const [muutmiseInfo, setMuutmiseInfo] = useState("");
    const [onSisselogitud, setOnSisseLogitud] = useState(false);
    const [hinnaMuutusLopp, setHinnaMuutusLopp] = useState(new Date());

    useEffect(() => {
        const getTooteInfo = async () => {
            const vastus = await getToode(id, localStorage.getItem('AuthToken') || "");
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

    const lopetaMuutmine = () => {
        setHinnaMuutmineNupusilt("Muuda hindu");
        setHinnaMuutmine(prev => !prev);
    }

    async function muudaHindu() {
        const sisselogitud = await authTeenus.kasSisselogitud();
        setOnSisseLogitud(sisselogitud);
        const lopetatiMuutmine = hinnaMuutmine;
        

        if (lopetatiMuutmine) {
            if (!sisselogitud || (hind === tooteInfo.tooteTukihind && uhikuHind === tooteInfo.tooteUhikuHind)) {
                lopetaMuutmine();
                return;
            }
            
            if (hinnaMuutusLopp.getTime() > new Date().getTime()) {
                const uusTooteInfo = tooteInfo;

                uusTooteInfo.tooteTukihind = hind;
                uusTooteInfo.tooteUhikuHind = uhikuHind;
                hinnaMuutusLopp.setHours(23, 59, 0, 0);
                uusTooteInfo.viimatiUuendatud = hinnaMuutusLopp.toISOString();

                

                const vastus = await uuendaTooteHind(tooteInfo, localStorage.getItem('AuthToken'));
                if (vastus.ok) {
                    setTooteInfo(uusTooteInfo);
                    lopetaMuutmine();
                } else {
                    setMuutmiseInfo("Tooteandmete uuendus ebaõnnestus.");
                }
            
            } else {
                setMuutmiseInfo("Kuupäev pole tulevikus!");
            }
        } else {
            setHinnaMuutmineNupusilt("Lõpeta muutmine");
            setHinnaMuutmine(prev => !prev);
            setHind(tooteInfo.tooteTukihind);
            setUhikuHind(tooteInfo.tooteUhikuHind);
            setMuutmiseInfo(sisselogitud ? "NB! Toote hinna muutmine mõjutab vaid sulle kuvatavat hinda" : "Hindade muutmiseks pead olema sisselogitud!");
        }
    }

    const kontrolliHinnaInput = (e, setter) => {
        let sisend = e.target.value;

        sisend = sisend.replace(',', '.');

        if (/^\d*\.?\d{0,2}$/.test(sisend)) {
            setter(sisend);
        }
    };

    const uuendusajaSilt = () => {
        if (tooteInfo.viimatiUuendatud) {
            return new Date(tooteInfo.viimatiUuendatud).getTime() > new Date().getTime() ? "Muudetud hind kuni:" : "Viimati uuendatud:";
        }

        return "Viimati uuendatud:";
    }

    return (
        <>
            <Menuu />
            <div id="sisu" className="hele">
                <div id="toode-konteiner">
                    <div className="umar-nurk tume" id="toode-andmed-konteiner">
                        <MuraFilter />
                        <div id='toote-andmed-row'>
                            <span className="hele-tekst pood">{tooteInfo.tooteNimi}</span>
                            <span className="hele-tekst">Hind: {tooteInfo.tooteTukihind}€</span>
                            { onSisselogitud && hinnaMuutmine && (<input name='hind' className='hele tume-tekst aja-sisend' value={hind} onChange={e => kontrolliHinnaInput(e, setHind)} /> ) }
                            <span className="hele-tekst">Ühikuhind: {tooteInfo.tooteUhikuHind}€/{tooteInfo.uhik}</span>
                            { onSisselogitud && hinnaMuutmine && (
                                <>
                                <input name='uhikuHind' className='hele tume-tekst aja-sisend' value={uhikuHind} onChange={e => kontrolliHinnaInput(e, setUhikuHind)} />
                                <div className="samal-real">
                                    <span className="hele-tekst">Hinnamuutuse kehtivuse lõpp:</span>
                                    <DatePicker className="kuupaevaValik" selected={hinnaMuutusLopp} onChange={(kuupaev) => setHinnaMuutusLopp(kuupaev)} dateFormat="dd-MM-yyyy"/>
                                </div>
                                </>) }
                            <span className="hele-tekst">{uuendusajaSilt()} {tooteInfo.viimatiUuendatud ? formaadiAeg(tooteInfo.viimatiUuendatud) : "Aeg puudub"}</span>
                            <button className='nupp tume2 hele-tekst' onClick={() => muudaHindu()}><span>{hinnaMuutmineNupusilt}</span></button>
                        </div>
                        <div id="pilt-konteiner" className="hele">
                            <img src={tooteInfo.toodePiltURL ? tooteInfo.toodePiltURL : standardPilt} alt="pilt" className={tooteInfo.toodePiltURL ? "toode-pilt-suur" : "toode-pilt-standard-suur"} />
                        </div>
                    </div>
                </div>
            </div>
        </>
    )
}

export default Toode;
