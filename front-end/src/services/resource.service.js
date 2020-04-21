import axios from 'axios';
import authHeader from './auth-header';

const API_URL = 'http://localhost:9004/services/';

class ResourceService {
    async getResources() {
        let user = JSON.parse(localStorage.getItem('user'));
        let organizationId = user.organizationId;

        return await axios
            .get(API_URL + 'organisation/' + organizationId + '/resources', { headers: authHeader() })
            .then(response => {
                return response.data;
            });
    }

    createResource(resource) {
        let user = JSON.parse(localStorage.getItem('user'));
        let organizationId = user.organizationId;

        return axios
            .post(API_URL + 'organisation/' + organizationId + '/resource', {
                name: resource.name,
            }, { headers: authHeader() })
            .then(response => {
                return response.data;
            });
    }

    deleteResource(resourceId){
        let user = JSON.parse(localStorage.getItem('user'));
        let organizationId = user.organizationId;

        return axios
            .delete(API_URL + 'organisation/' + organizationId + '/' + resourceId, { headers: authHeader() })
            .then(response => {
                return response.data;
            });
    }
}

export default new ResourceService();