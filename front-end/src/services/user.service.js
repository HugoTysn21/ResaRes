import axios from 'axios';
import authHeader from './auth-header';

const API_URL = 'http://localhost:9004/authentication/';

class UserService {
    async getUsers() {
        return await axios
            .get(API_URL + 'users', { headers: authHeader() })
            .then(response => {
                return response.data;
            });
    }

    async getUsersByOrganizationId() {
        let userLogged = JSON.parse(localStorage.getItem('user'));

        return await axios
            .get(API_URL + userLogged.organizationId + '/users', { headers: authHeader() })
            .then(response => {
                return response.data;
            });
    }

    async createEditorUser(user) {
        return await axios
            .post(API_URL + 'api/auth/user', {
                username: user.username,
                name: user.name,
                organizationId: user.organizationId,
                password: user.password,
                email: user.email
            }, { headers: authHeader() })
            .then(response => {
                return response.data;
            });
    }

    async createUser(user) {
        let userLogged = JSON.parse(localStorage.getItem('user'));

        return await axios
            .post(API_URL + 'api/auth/'+ userLogged.organizationId + '/user', {
                username: user.username,
                name: user.name,
                organizationId: userLogged.organizationId,
                password: user.password,
                email: user.email
            }, { headers: authHeader() })
            .then(response => {
                return response.data;
            });
    }
}

export default new UserService();