import {FormEvent, useReducer, useState} from "react";
import UserService, {RegisterRequest} from "../../lib/User";
import {Link} from "react-router-dom";
import {formReducer} from "../../lib/utils";
import styles from "../../Forms.module.css"

const initialFormData: RegisterRequest = {
    username: "",
    email: "",
    password: ""
}

export default function Register() {
    const [formData, setFormData] = useReducer(formReducer<RegisterRequest>, initialFormData);
    const [isRegistered, setIsRegistered] = useState(false);

    function handleFormSubmit(event: FormEvent) {
        event.preventDefault();
        console.log(formData);
        UserService.attemptRegister(formData)
            .then(() => setIsRegistered(true));
    }

    if (isRegistered) {
        return <>
            <h1>Rejestracja ukończona</h1>
            <p>Potwierdź adres mailowy przed zalogowaniem</p>
            <Link to="/user/login">Login</Link>
        </>;
    }

    return <div className={styles.formContainer}>
        <h1>Rejestracja</h1>

        <form onSubmit={handleFormSubmit}>
            <label>
                <p>Nazwa użytkownika:</p>
                <input name="username" type="text" onChange={setFormData}/>
            </label>
            <label>
                <p>Email:</p>
                <input name="email" type="email" onChange={setFormData}/>
            </label>
            <label>
                <p>Hasło:</p>
                <input name="password" type="password" onChange={setFormData}/>
            </label>
            <input className={styles.registerButton} type="submit" value="Rejestracja"/>
        </form>
    </div>
}
