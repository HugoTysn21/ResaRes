<template>
    <div class="col-10 page-container">
        <div class="page-container-row" v-if="reservations && reservations.length">
            <h1 class="color-blue">Mes réservations</h1>
            <v-card>
                <v-card-title>
                    Mes réservations
                    <v-spacer></v-spacer>
                    <v-text-field
                            v-model="reservationSearch"
                            append-icon="mdi-magnify"
                            label="Search"
                            single-line
                            hide-details
                    ></v-text-field>
                </v-card-title>
                <v-data-table
                        :headers="headers"
                        :items="reservations"
                        :search="reservationSearch"
                        :items-per-page=10
                        :page.sync="page"
                        hide-default-footer
                        @page-count="pageCount = $event"
                >
                    <template v-slot:item.actions="{ item }">
                        <v-icon
                                small
                                @click="submitDeleteReservation(item.id, item.name)"
                        >
                            mdi-delete
                        </v-icon>
                    </template>
                </v-data-table>
                <div class="text-center pt-2 mb-4">
                    <v-pagination v-model="page" class="mb-3" :length="pageCount"></v-pagination>
                </div>
            </v-card>
        </div>
        <div class="row" style="padding-left: 30px; padding-right: 30px;">
            <h1 class="color-blue" style="padding-left: 14px;">Nouvelle réservation</h1>
            <div class="col-12">
                <div>
                    <h4 class="mt-2">Liste des ressources</h4>
                    <div class="row d-flex">
                        <div class="col-4">
                            <v-card>
                                <v-card-title class="text-center">
                                    Affichez le détail d'une ressource pour créer une réservation
                                    <v-spacer></v-spacer>
                                    <v-text-field
                                            v-model="resourceSearch"
                                            append-icon="mdi-magnify"
                                            class="mt-5"
                                            label="Search"
                                            single-line
                                            hide-details
                                    ></v-text-field>
                                </v-card-title>
                                <v-data-table
                                        :headers="resourcesHeaders"
                                        :items="resources"
                                        :search="resourceSearch"
                                        :items-per-page=5
                                        :page.sync="resourcePage"
                                        hide-default-footer
                                        @page-count="resourcePageCount = $event"
                                >
                                    <template v-slot:item.actions="{ item }">
                                        <v-icon
                                                small
                                                @click="(getDetails(item.id, item.name))"
                                        >
                                            mdi-pencil
                                        </v-icon>
                                    </template>
                                </v-data-table>
                                <div class="text-center pt-2 mb-4">
                                    <v-pagination v-model="resourcePage" class="mb-3" :length="resourcePageCount"></v-pagination>
                                </div>
                            </v-card>
                        </div>
                        <div class="col-8">
                            <div v-if="attributes" class="card">
                                <button class="btn-card-close" v-on:click="closeDetails()">
                                    x
                                </button>
                                <div class="row d-flex">
                                    <div class="col-6">
                                        <table class="table" v-if="attributes && typeof attributes !== 'string'">
                                            <thead class="thead-dark">
                                            <tr>
                                                <th scope="col">Nom</th>
                                                <th scope="col">Valeur</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <tr v-for="attribute of attributes" :key="attribute.id">
                                                <td>{{ attribute.name }}</td>
                                                <td>{{ attribute.value }}</td>
                                            </tr>
                                            </tbody>
                                        </table>
                                        <p v-if="typeof attributes === 'string'">{{ attributes }}</p>
                                    </div>
                                    <div class="col-6">
                                        <h4 class="mb-3">Créer une réservation</h4>
                                        <form name="form" @submit.prevent="submitCreateReservation" data-vv-scope="form-attribute">
                                            <div class="form-group">
                                                <label for="name">Nom de la réservation</label>
                                                <p class="font-italic">Laissez vide : nom de la ressource</p>
                                                <input
                                                        v-model="reservation.name"
                                                        type="text"
                                                        class="form-control"
                                                        name="name"
                                                />
                                                <div
                                                        v-if="submitted && errors.has('name')"
                                                        class="alert-danger"
                                                >{{errors.first('name')}}</div>
                                            </div>
                                            <div class="form-group d-flex">
                                                <div>
                                                    <v-text-field
                                                            v-model="dateStart"
                                                            label="Date de début"
                                                            readonly
                                                            @click="startDateModal = true"
                                                    ></v-text-field>
                                                    <v-dialog
                                                            ref="startDateDialog"
                                                            v-model="startDateModal"
                                                            :return-value.sync="dateStart"
                                                            persistent
                                                            width="290px"
                                                    >
                                                        <v-date-picker v-model="dateStart" scrollable>
                                                            <v-spacer></v-spacer>
                                                            <v-btn text color="primary" @click="startDateModal = false">Annuler</v-btn>
                                                            <v-btn text color="primary" @click="$refs.startDateDialog.save(dateStart)">Ok</v-btn>
                                                        </v-date-picker>
                                                    </v-dialog>
                                                </div>
                                                <div class="ml-2">
                                                    <v-text-field label="Heure de fin" v-model="timeStart" readonly @click="startTimeModal = true"></v-text-field>
                                                    <v-dialog
                                                            ref="startTimeDialog"
                                                            v-model="startTimeModal"
                                                            width="290px"
                                                    >
                                                        <v-time-picker :format="format" v-model="timeStart">
                                                        </v-time-picker>
                                                    </v-dialog>
                                                </div>
                                            </div>
                                            <div class="form-group d-flex">
                                                <div>
                                                    <v-text-field
                                                            v-model="dateEnd"
                                                            label="Date de fin"
                                                            readonly
                                                            @click="endDateModal = true"
                                                    ></v-text-field>
                                                    <v-dialog
                                                            ref="endDateDialog"
                                                            v-model="endDateModal"
                                                            :return-value.sync="dateEnd"
                                                            persistent
                                                            width="290px"
                                                    >
                                                        <v-date-picker v-model="dateEnd" scrollable>
                                                            <v-spacer></v-spacer>
                                                            <v-btn text color="primary" @click="endDateModal = false">Annuler</v-btn>
                                                            <v-btn text color="primary" @click="$refs.endDateDialog.save(dateEnd)">Ok</v-btn>
                                                        </v-date-picker>
                                                    </v-dialog>
                                                </div>
                                                <div class="ml-2">
                                                    <v-text-field label="Heure de fin" v-model="timeEnd" readonly @click="endTimeModal = true"></v-text-field>
                                                    <v-dialog
                                                            ref="endTimeDialog"
                                                            v-model="endTimeModal"
                                                            width="290px"
                                                    >
                                                        <v-time-picker :format="format" v-model="timeEnd">
                                                        </v-time-picker>
                                                    </v-dialog>
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <button class="btn btn-primary btn-block">Créer</button>
                                                <p class="alert-success">{{ message }}</p>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import ResourcesService from '../services/resource.service';
    import AttributeService from '../services/attribute.service'
    import ReservationService from '../services/reservation.service';

    export default {
        name: 'Reservations',
        data() {
            return {
                resource: [],
                resources: [],
                reservation: [],
                reservations: [],
                attributes: null,
                selectedResourceId: null,
                selectedResourceName: null,
                submitted: false,
                attributeSubmitted: false,
                message: '',
                attributeMessage: '',
                startTimeModal: false,
                endTimeModal: false,
                startDateModal: false,
                endDateModal: false,
                dateStart: null,
                dateEnd: null,
                timeStart: null,
                timeEnd: null,
                format: '24hr',
                resourcePageCount: 0,
                resourcePage: 1,
                pageCount: 0,
                page: 1,
                resourceSearch: '',
                reservationSearch: '',
                resourcesHeaders: [
                    { text: '#', value: 'id' },
                    { text: 'Nom', value: 'name' },
                    { text: '', value: 'actions', sortable: false },
                ],
                headers: [
                    { text: '#', value: 'id' },
                    { text: 'Nom', value: 'name' },
                    { text: 'Début', value: 'start_date' },
                    { text: 'Fin', value: 'end_date' },
                    { text: 'Ressource', value: 'resourceId' },
                    { text: '', value: 'actions', sortable: false },
                ],
            }
        },
        computed: {
            loggedIn() {
                return this.$store.state.auth.status.loggedIn;
            },
        },
        // Fetches organizations when the component is created.
        async created() {
            if (!this.loggedIn) {
                await this.$router.push('/login');
            } else {
                try {
                    this.resources = await ResourcesService.getResources();
                } catch (e) {
                    console.error(e);
                }

                this.getReservations();
            }
        },
        methods: {
            async getReservations(){

                    this.reservations = await ReservationService.getReservation();

                    this.reservations.forEach(function(reservation) {
                        let startDate = reservation.start_date;
                        let endDate = reservation.end_date;

                        startDate = startDate.substring(0, startDate.length - 9);
                        startDate = new Date(startDate);
                        startDate = new Date(startDate.getTime() - 3000000);
                        startDate = startDate.getFullYear().toString()+"-"+((startDate.getMonth()+1).toString().length==2?(startDate.getMonth()+1).toString():"0"+(startDate.getMonth()+1).toString())+"-"+(startDate.getDate().toString().length==2?startDate.getDate().toString():"0"+startDate.getDate().toString())+" "+(startDate.getHours().toString().length==2?startDate.getHours().toString():"0"+startDate.getHours().toString())+":"+((parseInt(startDate.getMinutes()/5)*5).toString().length==2?(parseInt(startDate.getMinutes()/5)*5).toString():"0"+(parseInt(startDate.getMinutes()/5)*5).toString())+":00";

                        endDate = endDate.substring(0, endDate.length - 9);
                        endDate = new Date(endDate);
                        endDate = new Date(endDate.getTime() - 3000000);
                        endDate = endDate.getFullYear().toString()+"-"+((endDate.getMonth()+1).toString().length==2?(endDate.getMonth()+1).toString():"0"+(endDate.getMonth()+1).toString())+"-"+(endDate.getDate().toString().length==2?endDate.getDate().toString():"0"+endDate.getDate().toString())+" "+(endDate.getHours().toString().length==2?endDate.getHours().toString():"0"+endDate.getHours().toString())+":"+((parseInt(endDate.getMinutes()/5)*5).toString().length==2?(parseInt(endDate.getMinutes()/5)*5).toString():"0"+(parseInt(endDate.getMinutes()/5)*5).toString())+":00";

                        reservation.start_date = startDate;
                        reservation.end_date = endDate;
                    })

            },

            async getDetails(resourceId, resourceName) {
                this.attributes = await AttributeService.getAttributes(resourceId);
                this.selectedResourceId = resourceId;
                this.selectedResourceName = resourceName;

                if(!this.attributes.length){
                    this.attributes = "Cette ressource ne possède aucun attributs.";
                }
            },

            submitCreateReservation() {
                this.message = '';
                this.submitted = true;
                this.$validator.validate().then(isValid => {
                    if (isValid) {
                        if(!this.reservation.name){
                            this.reservation.name = this.selectedResourceName;
                        }

                        let startDate = this.dateStart + 'T' + this.timeStart + ':00';
                        startDate = new Date(startDate);

                        let endDate = this.dateEnd + 'T' + this.timeEnd + ':00';
                        endDate = new Date(endDate);

                        this.createReservation(startDate, endDate);
                    }
                });
            },

            async createReservation(startDate, endDate) {
                await ReservationService.createReservation(this.reservation, this.selectedResourceId, startDate, endDate);

                this.message = 'Réservation créée avec succès.';
                this.getReservations();
            },

            submitDeleteReservation(reservationId, reservationName){
                confirm('Êtes-vous sûr de vouloir supprimer la réservation "' + reservationName + '" ?') && this.deleteReservation(reservationId);
            },

            async deleteReservation(reservationId){
                await ReservationService.deleteReservation(reservationId);
                this.getReservations();
            },

            closeDetails() {
                this.message = '';
                this.selectedResourceId = null;
                this.selectedResourceName = null;
                this.attributes = null;
            },
        }
    }
</script>
<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>