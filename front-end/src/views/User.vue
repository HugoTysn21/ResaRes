<template>
    <div class="col-10 page-container">
        <h1 class="color-blue" style="padding-left: 30px;">Utilisateurs</h1>
        <div class="row page-container-row">
            <div class="col-8">
                <h3 class="mb-4">Liste des utilisateurs</h3>
                <p>Liste tous les utilisateurs de l'application</p>
                <v-card>
                    <v-card-title>
                        Liste de tous les utilisateurs de l'application
                        <v-spacer></v-spacer>
                        <v-text-field
                                v-model="search"
                                append-icon="mdi-magnify"
                                label="Search"
                                single-line
                                hide-details
                        ></v-text-field>
                    </v-card-title>
                    <v-data-table
                            :headers="headers"
                            :items="users"
                            :search="search"
                            :items-per-page=10
                            :page.sync="page"
                            hide-default-footer
                            @page-count="pageCount = $event"
                    ></v-data-table>
                    <div class="text-center pt-2 mb-4">
                        <v-pagination v-model="page" class="mb-3" :length="pageCount"></v-pagination>
                    </div>
                </v-card>
            </div>
            <form name="form" class="col-4" @submit.prevent="createUser">
                <h3 class="mb-4">Créer un utilisateur</h3>
                <p>Créé un utilisateur editeur de l'organisation </p>
                <div class="form-group">
                    <label for="name">Nom complet</label>
                    <input
                            v-model="user.name"
                            v-validate="'required|min:3|max:20'"
                            type="text"
                            class="form-control"
                            name="username"
                    />
                    <div
                            v-if="submitted && errors.has('username')"
                            class="alert-danger"
                    >{{errors.first('username')}}</div>
                </div>
                <div class="form-group">
                    <label for="username">Nom de compte</label>
                    <input
                            v-model="user.username"
                            v-validate="'required|min:3|max:20'"
                            type="text"
                            class="form-control"
                            name="username"
                    />
                    <div
                            v-if="submitted && errors.has('username')"
                            class="alert-danger"
                    >{{errors.first('username')}}</div>
                </div>
                <div class="form-group">
                    <label for="email">Email</label>
                    <input
                            v-model="user.email"
                            v-validate="'required|email|max:50'"
                            type="email"
                            class="form-control"
                            name="email"
                    />
                    <div
                            v-if="submitted && errors.has('email')"
                            class="alert-danger"
                    >{{errors.first('email')}}</div>
                </div>
                <div class="form-group">
                    <label for="password">Mot de passe</label>
                    <input
                            v-model="user.password"
                            v-validate="'required|min:6|max:40'"
                            type="password"
                            class="form-control"
                            name="password"
                    />
                    <div
                            v-if="submitted && errors.has('password')"
                            class="alert-danger"
                    >{{errors.first('password')}}</div>
                </div>
                <div class="form-group">
                    <label for="organizationId">Organisation</label><br>
                    <select name="organizationId" id="organizationId-select" style="width: 100%;" v-model="user.organizationId">
                        <option value="">Choisissez une organisation</option>
                        <option v-for="organization of organizations" :value="organization.id" :key="organization.id">
                            {{ organization.name }}
                        </option>
                    </select>
                </div>
                <div class="form-group">
                    <button class="btn btn-primary btn-block">Créer</button>
                </div>
            </form>
        </div>
    </div>
</template>

<script>
    import UserService from '../services/user.service';
    import OrganizationService from '../services/organization.service'
    import user from "../models/user";

    export default {
        name: 'User',
        data() {
            return {
                user: new user('', '', '', null),
                users: [],
                organizations: [],
                submitted: false,
                message: '',
                search: '',
                headers: [
                    { text: '#', value: 'id' },
                    { text: 'Nom complet', value: 'name' },
                    { text: 'Email', value: 'email' },
                    { text: 'Nom de compte', value: 'username' },
                    { text: 'Role', value: 'roles[0].name' },
                ],
                pageCount: 0,
                page: 1
            }
        },
        computed: {
            loggedIn() {
                return this.$store.state.auth.status.loggedIn;
            }
        },
        // Fetches organizations when the component is created.
        async created() {
            if (!this.loggedIn) {
                this.$router.push('/login');
            } else {
                try {
                    this.organizations = await OrganizationService.getOrganizations();
                } catch (e) {
                    console.error(e);
                }

                try {
                    this.users = await UserService.getUsers();
                } catch (e) {
                    console.error(e);
                }
            }
        },
        methods: {
            // Submit form and create organization
            createUser() {
                this.message = '';
                this.submitted = true;
                this.$validator.validate().then(isValid => {
                    if (isValid) {
                        UserService.createEditorUser(this.user);

                        this.message = 'Utilisateur créée avec succès.';
                    }
                });
            }
        }
    }
</script>
<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>