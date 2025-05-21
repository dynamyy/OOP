import React from 'react'
import Menuu from '../komponendid/Menuu'


function Kasutusjuhend() {
    return (
        <>
            <Menuu />
            <div id="sisu" className="hele kj">
                <div className=" tume-tekst" id="kasutusjuhend-konteiner">
                    <h1>Kuidas luua ostukorvi?</h1>
                    <div id="kasutusjuhend">
                        <div id='kj-1'>
                            <p className='kj-tekst'>Esmalt peate kasutaja looma ja sisse logima, et saaksite ostukorve salvestada. 
                                Kasutaja loomisel küsitakse teilt ka kliendikaarte, kuna tihtipeale saab kaartidega soodushindu. 
                                Siis liikuge lehele ostukorvid, kus saab näha juba olemasolevaid ostukorve ning luua uusi. Vajutage 
                                nuppu "loo ostukorv"</p>
                            <img className='kj-pilt' src="/staatiline/naide1.png" alt="Näide 1" />
                        </div>
                        <div id='kj-2'>
                            <img className='kj-pilt' src="/staatiline/naide2.png" alt="Näide 2" />
                            <p className='kj-tekst'>
                                Seejärel saate hakata ostukorvi lisama tooteid kasutades märksõnu. 
                                Märksõna saate lisada märksõnaväljale ning vajutades sisalduvuse nuppu saate valida, 
                                kas teie otsitav toode sisaldab või ei sisalda antud märksõna. Kui olete need valikud 
                                teinud vajutage "lisa märksõna" nuppu ning näeta kõrvalt kõiki tooteid, mis märksõnadele 
                                vastavad. Tootte kogust saab muuta vajutades pluss- ja miinusnuppe.
                            </p>
                        </div>
                        <div id='kj-3'>
                            <p className='kj-tekst'>
                                Tooteid saab filtreerida klikkides poodide logodele. Lisaks saab ebasobivaid tooteid valikust 
                                eemaldada vajutades ristikesele toote pildi ees. Ebasobivad tooted muutuvad punaseks ning neid 
                                odavaima toote leidmisel ei arvestata.
                            </p>
                            <img className='kj-pilt' src="/staatiline/naide3.png" alt="Näide 3" />
                        </div>
                        <div id='kj-4'>
                            <img className='kj-pilt' src="/staatiline/naide4.png" alt="Näide 4" />
                            <p className='kj-tekst'>
                                Kui olete tootevalikuga rahul vajutage nuppu "lisa ostukorvi" ning saate asuda uut toodet valima. 
                                Kui soovite juiba lisatud toodet uuesti muuta vajutage pliiatsi ikooni tootekaardil ostukorvis.
                                Kui kõik tooted on lisatud valige ostukorvile nimi ning vajutage nuppu "loo ostukorv". Siis suunatakse teid 
                                uuele lehele, kus kuvatakse iga poe odavaimad ostukorvid.
                            </p>
                        </div>
                        <div id='kj-5'>
                            <p className='kj-tekst'>
                                Ostukorvi lehel on igfal poel oma tulp ning saate visaalselt hindasid võrrelda. Kui mõnes poes kõiki tooteid ei leitud, kuvatakse vastav kiri tulbale.
                                Kui mõni toode mõnes poes teile ei sobi saate valida järgmise parima toote vajutades toote pildile. Kuna hinnad ajas muutuvad, siis saab ostukorve ka värskendada vajutades "värskenda" nuppu.
                            </p>
                            <img className='kj-pilt' src="/staatiline/naide5.png" alt="Näide 5" />
                        </div>
                        <div id='kj-6'>
                            <p className='kj-tekst'>
                                Kuna tihtipeale pakuvad poed ka personaalseid soodushindu, siis saab ostukorvi luues toote kaardile vajutades ka hinda muuta. Lisaks saab lisada hinnale kehtivuuse lõppkuupäeva.
                            </p>
                            <img className='kj-pilt' src="/staatiline/naide6.png" alt="Näide 6 " />
                        </div>
                    </div>
                </div>
            </div>
        </>
    )
}

export default Kasutusjuhend;
