class User {

    constructor() {
        this.login = null;
        this.token = null;
        //rights and roles
        this.rrs = [];

        this.load();

        console.log('INSTANTIATED User', this);
    }

    isToken() {
        return this.token != null;
    }

    isRr(rightOrRule) {

        for (const rr in this.rrs) {
            if (rightOrRule === rr) {
                return true;
            }
        }

        return false;
    }

    isLoggedIn() {
        return this.token != null;
    }

    login(login, token) {

        console.log('login, token', login, token);

        this.login = login;
        this.token = token;

        this.store();
    }

    logout() {

        this.login = null;
        this.token = null;
        this.rrs = [];

        this.store();
    }

    store() {

        localStorage.setItem('login', JSON.stringify(this, null, 2));
        console.log('localStorage stored', localStorage.getItem('login'));
    }

    load() {

        let item = localStorage.getItem('login');
        item = item == null ? null : JSON.parse(item);
        console.log('localStorage load', item);

        if (item == null) {
            return;
        }

        this.login = item.login != null ? item.login : this.login;
        this.token = item.token;
        this.rrs = item.rrs;
    }

    clear() {

        const login = this.login;

        this.login = null;
        this.token = null;
        this.rrs = [];
        this.store();

        if (login != null) {
            console.log('cleared user', login);
        }
    }
}

const user = new User();

export default user;