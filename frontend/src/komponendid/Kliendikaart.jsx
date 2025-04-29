import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'


function Kliendikaart(props) {

    return (
        <div className={`kliendikaart marksona-konteiner umar-nurk2 tume2 ${props.varv}`} onClick={() => {props.kaartValitud()}}>
            <span className='hele-tekst'>{props.poeNimi}</span>
        </div>
    )
}

export default Kliendikaart;