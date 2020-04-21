<template>
    <div class="col-10 page-container">
        <h1 class="color-blue" style="padding-left: 30px;">Ressources</h1>
        <div class="row page-container-row">
            <div class="col-12">
                <h4 class="mt-2">Créer une ressource</h4>
                <form name="form" class="d-flex" @submit.prevent="submitCreateResource('form-resource')" data-vv-scope="form-resource">
                    <div class="form-group d-flex flex-column">
                        <input
                                v-model="resource.name"
                                v-validate="'required|min:3|max:20'"
                                type="text"
                                style="height: 40px;"
                                class="form-control"
                                name="name"
                        />
                        <div
                                v-if="submitted && errors.items.length"
                                class="alert-danger" style="width: 100%"
                        >{{ errors.items[0].msg }}</div>
                    </div>
                    <div class="form-group d-flex" style="margin-left: 30px; height: 40px;">
                        <button class="btn btn-primary btn-block">Créer</button>
                    </div>
                </form>
                <p class="alert-success align-self-end">{{ message }}</p>
                <div>
                    <div class="row d-flex">
                        <div class="col-4">
                            <h4 class="mt-2">Liste des ressources</h4>
                            <p>Cliquez sur le pinceau pour afficher et créer les attributs d'une ressource.</p>
                            <v-card>
                                <v-card-title>
                                    <v-text-field
                                            v-model="search"
                                            append-icon="mdi-magnify"
                                            label="Search"
                                            single-line
                                            hide-details
                                    ></v-text-field>
                                </v-card-title>
                                <v-data-table
                                        :headers="resourcesHeaders"
                                        :items="resources"
                                        :search="search"
                                        :items-per-page=5
                                        :page.sync="resourcePage"
                                        hide-default-footer
                                        @page-count="resourcePageCount = $event"
                                >
                                    <template v-slot:item.actions="{ item }">
                                        <v-icon
                                                small
                                                class="mr-2"
                                                @click="getDetails(item.id, item.name)"
                                        >
                                            mdi-pencil
                                        </v-icon>
                                        <v-icon
                                                small
                                                @click="submitDeleteResource(item.id, item.name)"
                                        >
                                            mdi-delete
                                        </v-icon>
                                    </template>
                                </v-data-table>
                                <div class="text-center pt-2 mb-4">
                                    <v-pagination v-model="resourcePage" class="mb-3" :length="resourcePageCount"></v-pagination>
                                </div>
                            </v-card>
                        </div>
                        <div class="col-8">
                            <div v-if="attributes" class="card" style="margin-top: 105px;">
                                <button class="btn-card-close" v-on:click="closeDetails()">
                                    x
                                </button>
                                <div class="row d-flex">
                                    <div class="col-6">
                                        <h4>Liste des attributs</h4>
                                        <table class="table" v-if="attributes && typeof attributes !== 'string'">
                                            <thead class="thead-dark">
                                            <tr>
                                                <th scope="col">#</th>
                                                <th scope="col">Nom</th>
                                                <th scope="col">Valeur</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <tr v-for="attribute of attributes" :key="attribute.id">
                                                <td>{{ attribute.id }}</td>
                                                <td>{{ attribute.name }}</td>
                                                <td>{{ attribute.value }}</td>
                                            </tr>
                                            </tbody>
                                        </table>
                                        <p v-if="typeof attributes === 'string'">{{ attributes }}</p>
                                    </div>
                                    <div class="col-6">
                                        <h4>Créer un attribut</h4>
                                        <form name="form" @submit.prevent="submitCreateAttribute('form-attribute')" data-vv-scope="form-attribute">
                                            <div class="form-group">
                                                <label for="name">Nom</label>
                                                <input
                                                        v-model="attribute.name"
                                                        v-validate="'required|max:20'"
                                                        type="text"
                                                        class="form-control"
                                                        name="name"
                                                />
                                                <div
                                                        v-if="attributeSubmitted && errors.has('name')"
                                                        class="alert-danger"
                                                >{{errors.first('name')}}</div>
                                            </div>
                                            <div class="form-group">
                                                <label for="name">Valeur</label>
                                                <input
                                                        v-model="attribute.value"
                                                        v-validate="required"
                                                        type="text"
                                                        class="form-control"
                                                        name="name"
                                                />
                                                <div
                                                        v-if="attributeSubmitted && errors.has('name')"
                                                        class="alert-danger"
                                                >{{errors.first('name')}}</div>
                                            </div>
                                            <div class="form-group">
                                                <button class="btn btn-primary btn-block">Créer</button>
                                                <p class="alert-success">{{ attributeMessage }}</p>
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

    export default {
        name: 'Resources',
        data() {
            return {
                resource: [],
                resources: [],
                attribute: [],
                attributes: null,
                selectedResourceId: null,
                submitted: false,
                attributeSubmitted: false,
                message: '',
                search: '',
                attributeMessage: '',
                resourcePageCount: 0,
                resourcePage: 1,
                resourcesHeaders: [
                    { text: '#', value: 'id' },
                    { text: 'Nom', value: 'name' },
                    { text: 'Actions', value: 'actions', sortable: false },
                ],
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
                await this.$router.push('/login');
            }

            this.getResources();
        },
        methods: {
            async getResources(){
                try {
                    this.resources = await ResourcesService.getResources();
                } catch (e) {
                    console.error(e);
                }
            },

            submitCreateResource(scope) {
                this.message = '';
                this.submitted = true;
                this.$validator.validateAll(scope).then(isValid => {
                    if (isValid) {
                        this.createResource();
                    }
                });
            },

            async createResource() {
                await ResourcesService.createResource(this.resource);

                this.message = 'Ressource créée avec succès.';
                this.getResources();
            },

            async getDetails(resourceId) {
                this.attributes = await AttributeService.getAttributes(resourceId);
                this.selectedResourceId = resourceId;

                if(!this.attributes.length){
                    this.attributes = "Cette ressource ne possède aucun attributs.";
                }
            },

            submitCreateAttribute(scope) {
                console.log('submit');
                this.attributeMessage = '';
                this.attributeSubmitted = true;
                this.$validator.validateAll(scope).then(isValid => {
                    if (isValid) {
                        this.createAttribute();
                    }
                });
            },

            async createAttribute() {
                console.log('create');
                await AttributeService.createAttribute(this.attribute, this.selectedResourceId);

                this.attributeMessage = 'Attribut créée avec succès.';
                this.getDetails(this.selectedResourceId);
            },

            submitDeleteResource(resourceId, resourceName){
                confirm('Êtes-vous sûr de vouloir supprimer la ressource "' + resourceName + '" et tous ces attributs ?') && this.deleteResource(resourceId);
            },

            async deleteResource(resourceId){
                await ResourcesService.deleteResource(resourceId);
                this.getResources();
            },

            closeDetails() {
                this.attributeMessage = '';
                this.attributes = null;
                this.selectedResourceId = null;
            }
        }
    }
</script>
<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>