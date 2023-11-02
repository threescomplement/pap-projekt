export class User {
    username: string;
    password: string;
    token?: string;


    constructor(username: string, password: string) {
        this.username = username;
        this.password = password;
    }

    async requestToken() {
        try {
            const response = await fetch(`${process.env.REACT_APP_API_ROOT}auth/login`, {
                method: "POST",
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(this),
            });
            const json = await response.json();
            this.token = json.accessToken;
        } catch (e) {
            console.error(e)
        }
    }
}
