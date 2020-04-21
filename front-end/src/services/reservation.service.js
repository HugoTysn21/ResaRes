import axios from 'axios';
import authHeader from './auth-header';

const API_URL = 'http://localhost:9004/reservation/';

class ReservationService {
    async getReservation() {
        let user = JSON.parse(localStorage.getItem('user'));
        let userId = user.userId;

        return await axios
            .get(API_URL + '/' + userId + '/reservations', { headers: authHeader() })
            .then(response => {
                return response.data;
            });
    }

    createReservation(reservation, resourceId, startDate, endDate) {
        let user = JSON.parse(localStorage.getItem('user'));
        let organizationId = user.organizationId;
        let userId = user.id;

        return axios
            .post(API_URL + organizationId + '/' + resourceId + '/reservation', {
                name: reservation.name,
                resourceId: resourceId,
                userId: userId,
                start_date: startDate,
                end_date: endDate
            }, { headers: authHeader() })
            .then(response => {
                return response.data;
            });
    }

    deleteReservation(reservationId){
        return axios
            .delete(API_URL + 'delete/' + reservationId,{ headers: authHeader() })
            .then(response => {
                return response.data;
            });
    }
}

export default new ReservationService();