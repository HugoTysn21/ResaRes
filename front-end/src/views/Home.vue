<template>
    <div class="col-10 page-container">
        <div class="page-container-row" v-if="!isAdmin">
            <h1 class="color-blue" style="margin-left: 30px;">Dashboard</h1>
            <p style="margin-left: 30px;">Retrouvez le calendrier de vos réservation</p>
            <v-row class="fill-height page-container-row" v-if="!isAdmin">
                <v-col>
                    <v-sheet height="64">
                        <v-toolbar flat color="white">
                            <v-btn fab text small color="grey darken-2" @click="prev">
                                <v-icon small>mdi-chevron-left</v-icon>
                            </v-btn>
                            <v-btn fab text small color="grey darken-2" @click="next">
                                <v-icon small>mdi-chevron-right</v-icon>
                            </v-btn>
                            <v-spacer></v-spacer>
                        </v-toolbar>
                    </v-sheet>
                    <v-sheet height="600">
                        <v-calendar
                                ref="calendar"
                                v-model="focus"
                                color="primary"
                                :events="events"
                                :event-color="getEventColor"
                                :now="today"
                                :type="type"
                                @click:event="showEvent"
                                @change="getEvents"
                        ></v-calendar>
                        <v-menu
                                v-model="selectedOpen"
                                :close-on-content-click="false"
                                :activator="selectedElement"
                                offset-x
                        >
                            <v-card
                                    color="grey lighten-4"
                                    min-width="350px"
                                    flat
                            >
                                <v-toolbar
                                        :color="selectedEvent.color"
                                        dark
                                >
                                    <v-toolbar-title v-html="selectedEvent.name"></v-toolbar-title>
                                </v-toolbar>
                                <v-card-text>
                                    <strong>Date de début : </strong>{{ selectedEvent.start }}<br>
                                    <strong>Date de fin : </strong>{{ selectedEvent.end }}

                                </v-card-text>
                                <v-card-actions>
                                    <v-btn
                                            text
                                            color="secondary"
                                            @click="selectedOpen = false"
                                    >
                                        Fermer
                                    </v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-menu>
                    </v-sheet>
                </v-col>
            </v-row>
        </div>

        <div class="page-container-row mt-5" v-if="isAdmin">
            <h3>Bienvenue {{ user.name }}</h3>
            <p>Ici ce trouve la partie administration de l'application.</p>
            <ul>
                <li>Gère les organisations de l'application</li>
                <li>Gère les utilisateurs de l'application</li>
            </ul>
        </div>
    </div>
</template>

<script>
    import ReservationService from '../services/reservation.service'

    export default {
        name: 'Home',
        data: () => ({
            type: 'month',
            types: ['month', 'week', 'day', '4day'],
            reservations: [],
            mode: 'stack',
            modes: ['stack', 'column'],
            weekday: [0, 1, 2, 3, 4, 5, 6],
            weekdays: [
                { text: 'Sun - Sat', value: [0, 1, 2, 3, 4, 5, 6] },
                { text: 'Mon - Sun', value: [1, 2, 3, 4, 5, 6, 0] },
                { text: 'Mon - Fri', value: [1, 2, 3, 4, 5] },
                { text: 'Mon, Wed, Fri', value: [1, 3, 5] },
            ],
            value: '',
            events: [],
            colors: ['blue', 'indigo', 'deep-purple', 'cyan', 'green', 'orange', 'grey darken-1'],
            names: ['Meeting', 'Holiday', 'PTO', 'Travel', 'Event', 'Birthday', 'Conference', 'Party'],
            isAdmin: false,
            user: [],
            selectedEvent: {},
            selectedElement: null,
            selectedOpen: false,
        }),
        computed: {
            loggedIn() {
                return this.$store.state.auth.status.loggedIn;
            },
        },
        async created() {
            if (!this.loggedIn) {
                this.$router.push('/login');
            }

            this.user = JSON.parse(localStorage.getItem('user'));

            if(this.user){
                if(this.user.role === 'ROLE_ADMIN'){
                    this.isAdmin = true;
                }

                try {
                    this.reservations = await ReservationService.getReservation();

                    this.getEvents();
                } catch (e) {
                    console.error(e);
                }
            }
        },
        mounted () {
            this.$refs.calendar.checkChange()
        },
        methods: {
            getEvents () {
                const events = [];
                const eventCount = this.reservations.length;

                for (let i = 0; i < eventCount; i++) {
                    let startDate = this.formatDate(this.reservations[i].start_date);
                    let endDate = this.formatDate(this.reservations[i].end_date);

                    events.push({
                        name: this.reservations[i].name,
                        start: startDate,
                        end: endDate,
                        color: this.colors[this.rnd(0, this.colors.length - 1)],
                    })
                }

                this.events = events;
            },
            getEventColor (event) {
                return event.color
            },
            rnd (a, b) {
                return Math.floor((b - a + 1) * Math.random()) + a
            },
            formatDate (d) {
                d = d.substring(0, d.length - 9);

                d = new Date(d);
                d = new Date(d.getTime() - 3000000);

                return d.getFullYear().toString()+"-"+((d.getMonth()+1).toString().length==2?(d.getMonth()+1).toString():"0"+(d.getMonth()+1).toString())+"-"+(d.getDate().toString().length==2?d.getDate().toString():"0"+d.getDate().toString())+" "+(d.getHours().toString().length==2?d.getHours().toString():"0"+d.getHours().toString())+":"+((parseInt(d.getMinutes()/5)*5).toString().length==2?(parseInt(d.getMinutes()/5)*5).toString():"0"+(parseInt(d.getMinutes()/5)*5).toString())+":00";
            },

            showEvent ({ nativeEvent, event }) {
                console.log('show');
                const open = () => {
                    this.selectedEvent = event;
                    this.selectedElement = nativeEvent.target;
                    setTimeout(() => this.selectedOpen = true, 10);
                };

                if (this.selectedOpen) {
                    this.selectedOpen = false;
                    setTimeout(open, 10);
                } else {
                    console.log('open');
                    open();
                }
                nativeEvent.stopPropagation();
            },

            prev () {
                console.log('bjr');
                this.$refs.calendar.prev()
            },
            next () {
                this.$refs.calendar.next()
            },
        },
    };
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>