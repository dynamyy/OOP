import {React, useState, useEffect} from 'react';
import MuraFilter from './MuraFilter';

function OstukorvPoodTulp(props) {

    const summa = props.pood.tooted.map(toode => toode === null ? 0 : toode.tukiHind * toode.kogus).reduce((a, b) => a + b, 0);

    useEffect(() => {
        setTimeout(() => {
            const tulp = document.getElementById(props.pood.pood + "-id");
            tulp.style.height = "calc(" + (props.korgus > 0 ? 100 * props.korgus : 50) + "% - 2rem - 60.75px)" 
        }, 200);
    }, [props.korgus]);

    return (
        <div className="ostukorv-pood-konteiner" onClick={() => props.setAktiivnePood(props.pood)}>
            {summa > 0 ? <span className='tume-tekst'> {parseFloat(summa).toFixed(2)} €</span> : null}
            <div 
                id={props.pood.pood + "-id"} 
                className={"ostukorv-tulp tume umar-nurk" + (props.pood.tooted.includes(null) ? " punane" : "")}
            >
                <MuraFilter />
                {props.pood.tooted.includes(null) ? <span className='hele-tekst'> Kõiki tooteid ei leitud </span> : null}
            </div>
            <img src={props.logo} alt={props.pood.pood} className="logo-pilt-ostukorv" />
        </div>
    )
}

export default OstukorvPoodTulp;