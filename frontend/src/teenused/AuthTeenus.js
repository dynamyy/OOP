import { jwtDecode } from 'jwt-decode';
import { verifyToken, getKasutajaAndmed, setKasutajaAndmed, kustutaKasutaja } from '../teenused/api'

const AuthTeenus = {
    kasSisselogitud: async function() {
        const token = localStorage.getItem('AuthToken');

        if (!token) {
            return false;
        }

        const tokenAegunud = await this.kasTokenKehtib(token);

        if (!tokenAegunud) {
            localStorage.removeItem('AuthToken');
            console.log("frontend tuvastas tokeni aegumise");
            return false;
        }

        
        const vastus = await verifyToken(token);
        if (vastus.ok) {
            return true;
        } else {
            localStorage.removeItem('AuthToken');
            console.log("Tokeni valideerimine backendis failis: " + vastus.sonum);
            return false;
        }
    },


    kasTokenKehtib: function(token) {
        if (!token) {
            return false;
        }

        const tokenDecoded = jwtDecode(token);
        const aeg = Date.now() / 1000;

        return tokenDecoded.exp > aeg;
    },

    getKasutaja: function() {
        if (!this.kasSisselogitud()) {
            return "";
        }

        const token = localStorage.getItem('AuthToken');
        const tokenDecoded = jwtDecode(token);

        return tokenDecoded.sub;
    },

    setKliendikaardid: async function(uusListTuup) {
        const vastus = await setKasutajaAndmed(localStorage.getItem('AuthToken'), "kliendikaardid", "", uusListTuup);

        if (!vastus.ok) {
            console.log("Kasutaja kliendikaartide uuendamine ebaõnnestus");
        }
    },

    setParool: async function(uusListTuup) {
        const vastus = await setKasutajaAndmed(localStorage.getItem('AuthToken'), "parool", "", uusListTuup);
        
        return {ok:vastus.ok, sonum:vastus.sonum};
    },

    getKliendikaardid: async function() {
        const vastus = await getKasutajaAndmed(localStorage.getItem('AuthToken'), "kliendikaardid");
        if (vastus.ok) {
            return {"ok": vastus.ok, "kliendikaardid": new Set(vastus.sonum)};
        } else {
            console.log("Kasutaja andmete hankimine ebaõnnestus: " + vastus.sonum);
            localStorage.removeItem('AuthToken');
            return {"ok": vastus.ok, "kliendikaardid": new Set()};
        }
    },

    kustutaKasutaja: async function(parool) {
        const vastus = await kustutaKasutaja({token: localStorage.getItem('AuthToken'), parool: parool});
        return {ok: vastus.ok, sonum: vastus.sonum};
    }
};

export default AuthTeenus;