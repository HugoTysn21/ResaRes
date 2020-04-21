<template>
  <div class="col-10 page-container">
    <h1 class="color-blue" style="padding-left: 30px;">Organisations</h1>
    <div class="row page-container-row">
      <div class="col-8">
        <h3>Liste des organisations</h3>
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
                  :headers="organizationsHeaders"
                  :items="organizations"
                  :search="search"
                  :items-per-page=5
                  :page.sync="organizationsPage"
                  hide-default-footer
                  @page-count="organizationsPageCount = $event"
          >
          </v-data-table>
          <div class="text-center pt-2 mb-4">
            <v-pagination v-model="organizationsPage" class="mb-3" :length="organizationsPageCount"></v-pagination>
          </div>
        </v-card>
      </div>
      <form name="form" class="col-4" @submit.prevent="createSubmitOrganization">
        <h3>Créer une organisation</h3>
        <div class="form-group">
          <label for="name">Nom de l'organisation</label>
          <input
                  v-model="organization.name"
                  v-validate="'required|min:3|max:20'"
                  type="text"
                  class="form-control"
                  name="name"
          />
          <div
                  v-if="submitted && errors.has('name')"
                  class="alert-danger"
          >{{errors.first('name')}}</div>
        </div>
        <div class="form-group">
          <button class="btn btn-primary btn-block">Créer</button>
          <p class="alert-success">{{ message }}</p>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
  import OrganizationService from '../services/organization.service';
  import Organization from "../models/organization";

  export default {
    name: 'Organizations',
    data() {
      return {
        organization: new Organization(''),
        organizations: [],
        submitted: false,
        message: '',
        search: '',
        organizationsPageCount: 0,
        organizationsPage: 1,
        organizationsHeaders: [
          { text: '#', value: 'id' },
          { text: 'Nom', value: 'name' },
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
        this.$router.push('/login');
      } else {
        this.getOrganizations();
      }
    },
    methods: {
      async getOrganizations(){
        try {
          this.organizations = await OrganizationService.getOrganizations()
        } catch (e) {
          console.error(e)
        }
      },
      // Submit form and create organization
      createSubmitOrganization() {
        this.message = '';
        this.submitted = true;
        this.$validator.validate().then(isValid => {
          if (isValid) {
            this.createOrganization();
          }
        });
      },

      async createOrganization() {
        await OrganizationService.createOrganizations(this.organization);

        this.message = 'Organisation créée avec succès.';
        this.getOrganizations()
      }
    }
  }
</script>
<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>