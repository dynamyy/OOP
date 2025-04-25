import { jwtDecode } from 'jwt-decode';

const AuthTeenus = {
    kasSisselogitud: function() {
        // Hetkel vaid ajalise aegumise kontroll
        return this.kasTokenKehtib();
    },


    kasTokenKehtib: function() {
        const token = localStorage.getItem('AuthToken');

        if (!token) {
            return false;
        }

        const tokenDecoded = jwtDecode(token);
        const aeg = Date.now() / 1000;

        return tokenDecoded.exp > aeg;
    }
};

export default AuthTeenus;