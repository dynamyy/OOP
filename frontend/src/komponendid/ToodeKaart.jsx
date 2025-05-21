import { useState, React } from 'react'
import { useNavigate } from 'react-router-dom';
import standardPilt from '../staatiline/standard/standard-toode.png';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faXmarkCircle } from '@fortawesome/free-regular-svg-icons'
import '../staatiline/UusOstukorv.css'

function ToodeKaart(props) {
    const navigate = useNavigate();

    const kaartVajutatud = (e) => {
        navigate(`/toode/${props.id}`);
    }

    const Xvajutatud = (e) => {
        e.stopPropagation();
        props.lisaEbasobivToode(e, props.id);
    }

    console.log(props.ebasobivadTooted)
    console.log(props.id)

    return (
        <div>
            <div onClick={(e) => kaartVajutatud(e)} className={"toode-kaart-konteiner hele umar-nurk" + (JSON.parse(localStorage.getItem('EbasobivadTooted')).includes(props.id) ? " ebasobiv-toode" : "")}>
                <div className="toode-kaart-pilt-konteiner">
                    <FontAwesomeIcon icon={faXmarkCircle} className='ikoon toode-kaart-ikoon' onClick={Xvajutatud} />
                    <div className='hele umar-nurk'>
                        <img src={props.toodeUrl ? props.toodeUrl : standardPilt} alt="pilt" className={props.toodeUrl ? "toode-pilt" : "toode-pilt-standard"} />
                    </div>
                    <span className="tume-tekst">{props.tooteNimetus}</span>
                </div>
                <div className="toode-kaart-hind-konteiner">
                    <img src={props.poodPilt} alt="logo" className="logo-pilt" />
                    <span className="tume-tekst">{parseFloat(props.tukiHind).toFixed(2)} €</span>
                    <span className="tume-tekst">{parseFloat(props.uhikuHind).toFixed(2)} €/{props.uhik}</span>
                </div>
            </div>
        </div>
    )
}

export default ToodeKaart;