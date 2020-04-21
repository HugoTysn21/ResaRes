import axios from 'axios';
import authHeader from './auth-header';

const API_URL = 'http://localhost:9004/services/';

class AttributeService {
    async getAttributes(resourceId) {
        return await axios
            .get(API_URL + 'resources/' + resourceId + '/attributes', { headers: authHeader() })
            .then(response => {
                return response.data;
            });
    }

    async createAttribute(attribute, resourceId) {
        return await axios
            .post(API_URL + 'resources/' + resourceId + '/attribute', {
                name: attribute.name,
                value: attribute.value,
                type: 0
            }, { headers: authHeader() })
            .then(response => {
                return response.data;
            });
    }
}

export default new AttributeService();