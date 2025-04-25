import { jwtDecode } from 'jwt-decode';
import { verifyToken } from '../teenused/api'

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
    }
};

export default AuthTeenus;