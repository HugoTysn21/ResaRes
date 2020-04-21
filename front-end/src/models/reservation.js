export default class Reservation {
    constructor(name, start, end, resourceId, userId, organizationId) {
        this.name = name;
        this.start = start;
        this.end = end;
        this.resourceId = resourceId;
        this.userId = userId;
        this.organizationId = organizationId;
    }
}