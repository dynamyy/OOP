import {React, useState, useEffect} from 'react';

function OstukorvPoodTulp(props) {

    const summa = props.pood.tooted.map(toode => toode === null ? 0 : toode.tukiHind * toode.kogus).reduce((a, b) => a + b, 0);
    const [hover, setHover] = useState(false)

    useEffect(() => {
        setTimeout(() => {
            const tulp = document.getElementById(props.pood.pood + "-id")
            tulp.style.height = (props.korgus > 0 ? 60 * props.korgus : 20) + "%"
        }, 200)
    }, [props.korgus])



    return (
        <div className="ostukorv-pood-konteiner" >
            {summa > 0 ? <span className='tume-tekst'> {parseFloat(summa).toFixed(2)} €</span> : null}
            <div 
                id={props.pood.pood + "-id"} 
                className="ostukorv-tulp tume umar-nurk"
                onMouseEnter={() => setHover(true)}
                onMouseLeave={() => setHover(false)}
            >
                {summa === 0 ? <span className='hele-tekst'> Kõiki tooteid ei leitud </span> : null}
                <div 
                    className="ostukorv-pood-tooted tume hele-tekst umar-nurk"
                    style={{ display: hover ? "flex" : "none" }}
                >
                    <span>OSTUKORV</span>
                    {props.pood.tooted.map((toode) => {
                        console.log(toode)
                        return <span>{toode !== null ? toode.nimetus : ""}</span>
                    })}
                </div>
            </div>
            <img src={props.logo} alt={props.pood.pood} className="logo-pilt-ostukorv" />
        </div>
    )
}

export default OstukorvPoodTulp;