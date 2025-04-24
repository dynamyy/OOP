const BAAS_URL = "http://localhost:8080/api"

export const postSisseLogimine = async (email, parool) => {
    try {
        const vastus = await fetch(`${BAAS_URL}/sisse-logimine`, {
        method: "POST",
        headers: { "Content-Type": "application/json"},
        body: JSON.stringify({email, parool})
        });
    
        const vastuseData = await vastus.json();
        return await {ok: vastus.ok, sonum: vastuseData.sonum};

    } catch (viga) {
        console.error("Viga sisselogimisega: ", viga.message);
    }
}

export const postRegistreerimine = async (email, parool) => {
    try {
        const vastus = await fetch(`${BAAS_URL}/registreeri`, {
            method: "POST",
            headers: { "Content-Type": "application/json"},
            body: JSON.stringify({email, parool})
        })

        const vastuseData = await vastus.json();
        return {ok: vastus.ok, sonum: vastuseData.sonum};

    } catch (viga) {
        console.error("Viga registreerimsiel ", viga.message);
    }
}