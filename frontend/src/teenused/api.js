const BAAS_URL = "http://localhost:8080/api"

export const postSisseLogimine = async (email, parool) => {
    try {
        const vastus = await fetch(`${BAAS_URL}/sisse-logimine`, {
        method: "POST",
        headers: { "Content-Type": "application/json"},
        body: JSON.stringify({email, parool})
        });
    
        const vastuseData = await vastus.json();

        if (vastus.ok) {
            return {ok: vastus.ok, sonum: vastuseData.sonum, token: vastuseData.token};
        } else {
            return {ok: vastus.ok, sonum: vastuseData.sonum};
        }

    } catch (viga) {
        console.error("Viga sisselogimisega: ", viga.message);
    }
}

export const postRegistreerimine = async (email, parool, kliendikaardid) => {
    try {
        const vastus = await fetch(`${BAAS_URL}/registreeri`, {
            method: "POST",
            headers: { "Content-Type": "application/json"},
            body: JSON.stringify({email, parool, kliendikaardid})
        })

        const vastuseData = await vastus.json();
        return {ok: vastus.ok, sonum: vastuseData.sonum};

    } catch (viga) {
        console.error("Viga registreerimsiel ", viga.message);
    }
}

export const verifyToken = async (token) => {
    try {
        const vastus = await fetch(`${BAAS_URL}/tokenVerif`, {
            method: "POST",
            headers: { "Content-Type": "application/json"},
            body: JSON.stringify({token})
        })

        const vastuseData = await vastus.json();
        return {ok: vastus.ok, sonum: vastuseData.sonum};

    } catch (viga) {
        console.error("Viga tokeni kontrollimisel", viga.message);
    }
}

export const postMarksonad = async (marksonad) => {
    try {
        const marksonadList = Object.entries(marksonad).map(([märksõna, valikuVärv]) => ({
            märksõna,
            valikuVärv
        }));

        const vastus = await fetch(`${BAAS_URL}/tooted`, {
            method: "POST",
            headers: { "Content-Type": "application/json"},
            body: JSON.stringify(marksonadList)
        })

        const vastuseAndmed = await vastus.json();

        if (vastus.ok) {
            return {ok: true, marksonad: vastuseAndmed}
        } else {
            return {ok: false, marksonad: {}}
        }
       
    } catch (viga) {
        console.log("Viga märksõnade saatmisel ", viga.message)
        return {ok: false, marksonad: {}}
    }
}