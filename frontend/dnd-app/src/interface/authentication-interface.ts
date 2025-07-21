export class CreateUserDTO {
    
    username : string;
    password : string;

    constructor(username : string, password : string) {
        this.username = username;
        this.password = password;
    }

    getUsername() {
        return this.username;
    }

    setUsername(username : string) {
        this.username = username;
    }

    getPassword() {
        return this.password;
    }

    setPassword(password : string) {
        this.password = password;
    }


}