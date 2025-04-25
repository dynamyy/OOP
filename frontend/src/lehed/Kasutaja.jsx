import React, { useEffect, useState } from 'react'
import Menuu from '../komponendid/Menuu'
import LoginAken from '../komponendid/LoginAken'
import authTeenus from '../teenused/AuthTeenus'

function Tooted() {
    const [onSisselogitud, setOnSisseLogitud] = useState(false);
    

    useEffect(() => {
        setOnSisseLogitud(authTeenus.kasSisselogitud());
    }, []);


    if (!onSisselogitud) {
        return <LoginAken />;
    }


    return (
        <>
            <Menuu />
            <div id="sisu" className="hele">
                <div className="tume umar-nurk hele-tekst" id="s-log">
                    <h2>Kasutaja (sisse logitud)</h2>
                </div>
            </div>
        </>
    )
}

export default Tooted;
