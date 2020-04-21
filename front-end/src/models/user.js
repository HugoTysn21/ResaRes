export default class User {
    constructor(username, email, name, password, organizationId) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.password = password;
        this.organizationId = organizationId;
    }
}