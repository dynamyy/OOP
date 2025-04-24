const BAAS_URL = "http://localhost:8080/api"

export const postSisseLogimine = async (email, parool) => {
    fetch(`${BAAS_URL}/sisse-logimine`, {
        method: "POST",
        headers: { "Content-Type": "application/json"},
        body: JSON.stringify({email, parool})
    })
}

export const postRegistreerimine = async (email, parool) => {
    fetch(`${BAAS_URL}/registreeri`, {
        method: "POST",
        headers: { "Content-Type": "application/json"},
        body: JSON.stringify({email, parool})
    })
}