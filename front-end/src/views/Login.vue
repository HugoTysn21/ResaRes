<template>
    <div class="col-10 page-container">
        <div class="card card-container">
            <h3 class="mb-4">Connexion</h3>
            <form name="form" @submit.prevent="handleLogin">
                <div class="form-group">
                    <label for="username">Nom d'utilisateur</label>
                    <input
                            v-model="user.username"
                            v-validate="'required'"
                            type="text"
                            class="form-control"
                            name="username"
                    />
                    <div
                            v-if="errors.has('username')"
                            class="alert alert-danger mt-2"
                            role="alert"
                    >Nom d'utilisateur requis.</div>
                </div>
                <div class="form-group">
                    <label for="password">Mot de passe</label>
                    <input
                            v-model="user.password"
                            v-validate="'required'"
                            type="password"
                            class="form-control"
                            name="password"
                    />
                    <div
                            v-if="errors.has('password')"
                            class="alert alert-danger mt-2"
                            role="alert"
                    >Mot de passe requis.</div>
                </div>
                <div class="form-group">
                    <button class="btn btn-primary btn-block" :disabled="loading">
                        <span v-show="loading" class="spinner-border spinner-border-sm"></span>
                        <span>Connexion</span>
                    </button>
                </div>
                <div class="form-group">
                    <div v-if="message" class="alert alert-danger" role="alert">{{message}}</div>
                </div>
            </form>
        </div>
    </div>
</template>

<script>
    import User from '../models/user';

    export default {
        name: 'Login',
        data() {
            return {
                user: new User('', ''),
                loading: false,
                message: ''
            };
        },
        computed: {
            loggedIn() {
                return this.$store.state.auth.status.loggedIn;
            }
        },
        created() {
            if (this.loggedIn) {
                this.$router.push('/');
            }
        },
        methods: {
            handleLogin() {
                this.loading = true;
                this.$validator.validateAll().then(isValid => {
                    if (!isValid) {
                        this.loading = false;
                        return;
                    }

                    if (this.user.username && this.user.password) {
                        this.$store.dispatch('auth/login', this.user).then(
                            () => {
                                this.$router.push('/');
                            },
                            error => {
                                this.loading = false;
                                this.message = error.response.data.message ||
                                    (error.response && error.response.data) ||
                                    error.message ||
                                    error.toString();
                            }
                        );
                    }
                });
            }
        }
    };
</script>

<style scoped>
    label {
        display: block;
        margin-top: 10px;
    }

    .card {
        background-color: #ebeaf0;
        padding: 20px 25px 30px;
        margin: 0 auto;
        margin-top: 14%;
        max-width: 350px;
        -moz-border-radius: 5px;
        -webkit-border-radius: 5px;
        border-radius: 5px;
        border: 1px solid #2a3e8b;
        -moz-box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
        -webkit-box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
        box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
    }
</style>