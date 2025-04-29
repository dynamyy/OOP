import React, { useEffect, useState } from 'react'
import Menuu from '../komponendid/Menuu'
import LoginAken from '../komponendid/LoginAken'
import Kliendikaart from '../komponendid/Kliendikaart'
import authTeenus from '../teenused/AuthTeenus'

function Tooted() {
    const [onSisselogitud, setOnSisseLogitud] = useState(false);
    const [kasutaja, setKasutaja] = useState('');
    

    useEffect(() => {
        const checkSisselogitud = async () => {
            const isLoggedIn = await authTeenus.kasSisselogitud();
            setOnSisseLogitud(isLoggedIn);

            if (isLoggedIn) {
                const kasutajaNimi = await authTeenus.getKasutaja();
                setKasutaja(kasutajaNimi);
            }
        };

        checkSisselogitud();
    }, []);


    if (!onSisselogitud) {
        return <LoginAken />;
    }


    return (
        <>
            <Menuu />
            <div id="sisu" className="hele">
                <div className="tume umar-nurk hele-tekst" id="s-log">
                    <h2>Kasutaja {kasutaja}</h2>
                    
                    <h2>Kliendikaardid:</h2>
                    <div className="kliendikaardid samal-real">
                        <Kliendikaart poeNimi="COOP" varv="roheline"/>
                        <Kliendikaart poeNimi="Prisma" varv="roheline"/>
                        <Kliendikaart poeNimi="Rimi" varv="roheline"/>
                        <Kliendikaart poeNimi="Maxima" varv="roheline"/>
                        <Kliendikaart poeNimi="Selver" varv="roheline"/>
                        <button className='nupp tume2 hele-tekst' onClick={() => console.log("nupp")}><span>Muuda</span></button>
                    </div>

                    <div className="samal-real">
                        <button className='nupp tume2 hele-tekst' onClick={() => console.log("nupp")}><span>Muuda parooli</span></button>
                        <button className='nupp tume2 hele-tekst' onClick={() => console.log("nupp")}><span>Kustuta konto</span></button>
                    </div>
                </div>
            </div>
        </>
    )
}

export default Tooted;
