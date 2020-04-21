import axios from 'axios';
import authHeader from './auth-header';

const API_URL = 'http://localhost:9004/services/';

class OrganizationService {
    async getOrganizations() {
        return await axios
            .get(API_URL + 'organisations', { headers: authHeader() })
            .then(response => {
                return response.data;
            });
    }

    createOrganizations(organization) {
        return axios
            .post(API_URL + 'organisation', {
                name: organization.name,
            }, { headers: authHeader() })
            .then(response => {
                return response.data;
            });
    }
}

export default new OrganizationService();