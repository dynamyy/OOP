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

export const getKasutajaAndmed = async (token, andmetuup) => {
    try {
        const vastus = await fetch(`${BAAS_URL}/kasutaja`, {
            method: "POST",
            headers: { "Content-Type": "application/json"},
            body: JSON.stringify({"token":token, "tegevus":"get", "andmetuup":andmetuup})
        })

        const vastuseData = await vastus.json();
        return {ok: vastus.ok, sonum: vastuseData.sonum};

    } catch (viga) {
        console.error("Viga kasutaja andmete hankimisel", viga.message);
    }
}

export const setKasutajaAndmed = async (token, andmetuup, uusSoneTuup, uusListTuup) => {
    try {
        const vastus = await fetch(`${BAAS_URL}/kasutaja`, {
            method: "POST",
            headers: { "Content-Type": "application/json"},
            body: JSON.stringify({"token":token, "tegevus":"set", "andmetuup":andmetuup, "uusSoneTuup":uusSoneTuup, "uusListTuup":uusListTuup})
        })

        const vastuseData = await vastus.json();
        return {ok: vastus.ok, sonum: vastuseData.sonum};

    } catch (viga) {
        console.error("Viga kasutaja andmete uuendamisel", viga.message);
    }
}

export const postMarksonad = async (marksonad, nihe, token) => {
    try {
        const marksonadList = Object.entries(marksonad).map(([marksona, valikuVarv]) => ({
            marksona,
            valikuVarv
        }));

        const vastus = await fetch(`${BAAS_URL}/tooted`, {
            method: "POST",
            headers: { "Content-Type": "application/json"},
            body: JSON.stringify({"marksonad": marksonadList, "nihe": nihe, token: {"token": token}})
        })

        const vastuseAndmed = await vastus.json();

        if (vastus.ok) {
            return {ok: true, kuvaTootedDTO: vastuseAndmed}
        } else {
            return {ok: false, kuvaTootedDTO: {}}
        }
       
    } catch (viga) {
        console.log("Viga märksõnade saatmisel ", viga.message)
        return {ok: false, marksonad: {}}
    }
}

export const postOstukorv = async (nimi, tooted, token) => {
    console.log({"nimi": nimi, "tooted": tooted})
    try {
        const vastus = await fetch(`${BAAS_URL}/ostukorv`, {
            method: "POST",
            headers: { "Content-Type": "application/json"},
            body: JSON.stringify({"nimi": nimi, "tooted": tooted, "token": token || ""})
        })

        const sonum = await vastus.json();

        if (vastus.ok) {
            return {ok: true, sonum: sonum}
        } else {
            return {ok: false, sonum: sonum}
        }
    } catch (viga) {
        console.log("Viga ostukorvi saatmisel ", viga.message)
        return {ok: false, ostukorv: "Ilmnes viga ostukorvi saatmisel"}
    }
}

export const kustutaKasutaja = async (requestData) => {
    try {
        const vastus = await fetch(`${BAAS_URL}/kustutaKasutaja`, {
            method: "POST",
            headers: { "Content-Type": "application/json"},
            body: JSON.stringify({"token": requestData.token, "parool": requestData.parool})
        })

        const vastuseData = await vastus.json();
        return {ok: vastus.ok, sonum: vastuseData.sonum};
    } catch (viga) {
        return {ok: false, sonum: "Viga kasutaja kustutamise päringu saatmisel"}
    }
}

export const getToode = async (id, token) => {
    try {
        const vastus = await fetch(`${BAAS_URL}/toode/${id}`, {
            method: "POST",
            headers: { "Content-Type": "application/json"},
            body: JSON.stringify({"token": token})
        })

        const vastuseData = await vastus.json();

        if (vastus.ok) {
            return {ok: vastus.ok, tooteAndmed: vastuseData.tooteAndmed};
        }
        
        return {ok: vastus.ok, sonum: vastuseData.sonum};
        
    } catch (viga) {
        return {ok: false, sonum: "Viga tooteandmete hankimisel"}
    }
}

export const uuendaTooteHind = async (tooteInfo, token) => {
    try {
        const vastus = await fetch(`${BAAS_URL}/toode/muuda`, {
            method: "POST",
            headers: { "Content-Type": "application/json"},
            body: JSON.stringify({"token": token, "toodeDTO": tooteInfo})
        })

        const vastuseData = await vastus.json();

        return {ok: vastus.ok, sonum: vastuseData.sonum};
    } catch (viga) {
        return {ok: false, sonum: "Viga toote uuendamisel"}
    }
}

export const getOstukorvTulemus = async (id, token) => {
    try {
        const vastus = await fetch(`${BAAS_URL}/ostukorv/${id}`, {
            method: "POST",
            headers: { "Content-Type": "application/json"},
            body: JSON.stringify({"token": token || ""})
        })

        const vastuseData = await vastus.json();

        if (vastus.ok) {
            return {ok: vastus.ok, ostukorvAndmed: vastuseData.ostukorvAndmed};
        }
        
        return {ok: vastus.ok, sonum: vastuseData.sonum};
        
    } catch (viga) {
        return {ok: false, sonum: "Viga ostukorvi andmete hankimisel"}
    }
}

export const uuendaOstukorvi = async (id, token) => {
    try {
        const vastus = await fetch(`${BAAS_URL}/ostukorv/uuenda`, {
            method: "POST",
            headers: { "Content-Type": "application/json"},
            body: JSON.stringify({"id": id, "token": token || ""})
        })

        const vastuseData = await vastus.json()

        if (vastus.ok) {
            return {ok: vastus.ok, sonum: vastuseData.sonum};
        }
        
        return {ok: vastus.ok, sonum: vastuseData.sonum};
    } catch (viga) {
        return {ok: false, sonum: "Viga ostukorvi andmete hankimisel"}  
    }
}

export const getOstukorvNimed = async(token) => {
    try {
        const vastus = await fetch(`${BAAS_URL}/ostukorv/nimed`, {
            method: "POST",
            headers: { "Content-Type": "application/json"},
            body: JSON.stringify({"token": token || ""})
        })

        const vastuseData = await vastus.json()

        if (vastus.ok) {
            return {ok: vastus.ok, ostukorvid: vastuseData.ostukorvid};
        }
        
        return {ok: vastus.ok, sonum: vastuseData.sonum};
    } catch (viga) {
        return {ok: false, sonum: "Viga ostukorvi andmete hankimisel"}  
    }
}

export const kustutaOstukorv = async (id, token) => {
    try {
        const vastus = await fetch(`${BAAS_URL}/kustuta-ostukorv/${id}`, {
            method: "POST",
            headers: { "Content-Type": "application/json"},
            body: JSON.stringify({"id": id, "token": token || ""})
        })

        const vastuseData = await vastus.json()

        if (vastus.ok) {
            return {ok: vastus.ok, sonum: vastuseData.sonum};
        }
        
        return {ok: vastus.ok, sonum: vastuseData.sonum};
    } catch (viga) {
        return {ok: false, sonum: "Viga ostukorvi andmete hankimisel"}  
    }
}