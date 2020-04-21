<template>
    <div id="menu" class="col-2 d-flex flex-column">
        <div class="menu-card menu-card_logo text-center">
            <img alt="logo" style="width:100%;" src="../assets/logo.png">
            <a v-if="loggedIn" class="menu-card_link mt-2" href @click.prevent="logOut">
                <i class="fas fa-sign-out-alt"></i> Déconnexion
            </a>
        </div>
        <router-link to="/" v-if="loggedIn">
            <div class="menu-card menu-card_link">
            <i class="fas fa-home"></i><br> Dashboard
        </div>
        </router-link>
        <router-link to="/organizations" v-if="role === 'ROLE_ADMIN'">
            <div class="menu-card menu-card_link">
                <i class="fas fa-sitemap"></i><br> Organisations
            </div>
        </router-link>
        <router-link to="/organization" v-if="role === 'ROLE_EDITOR'">
            <div class="menu-card menu-card_link">
                <i class="fas fa-cog"></i><br> Organisation
            </div>
        </router-link>
        <router-link to="/users" v-if="role === 'ROLE_ADMIN'">
            <div class="menu-card menu-card_link">
                <i class="fas fa-users"></i><br> Utilisateurs
            </div>
        </router-link>
        <router-link to="/resources" v-if="role === 'ROLE_EDITOR'" >
            <div class="menu-card menu-card_link">
                <i class="fas fa-toolbox"></i><br> Ressources
            </div>
        </router-link>
        <router-link to="/reservations" v-if="role === 'ROLE_USER' || role === 'ROLE_EDITOR'">
            <div class="menu-card menu-card_link">
                <i class="far fa-calendar-alt"></i><br> Réservations
            </div>
        </router-link>
    </div>
</template>

<script>
export default {
    data() {
        return {
            isLoggin: 0,
            role: ''
        }
    },
    computed: {
        loggedIn() {
            return this.$store.state.auth.status.loggedIn;
        }
    },
    created() {
        if (this.loggedIn) {
            this.isLoggin = 1;

            let user = JSON.parse(localStorage.getItem('user'));
            this.role = user.role;
        }
    },
    updated() {
        let user = JSON.parse(localStorage.getItem('user'));
        if(!user){
            this.role = ''
        } else {
            this.role = user.role;
        }
    },
    methods: {
        logOut() {
            this.$store.dispatch('auth/logout');
            this.$router.push('/login');
        }
    }
}
</script>