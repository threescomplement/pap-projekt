import {FormEvent, useReducer, useState} from "react";
import {attemptLogin, LoginRequest} from "../../lib/User";
import useUser from "../../hooks/useUser";
import {useNavigate} from "react-router-dom";
import {formReducer} from "../../lib/utils";
import styles from "./Login.module.css"
import ErrorBox from "../../components/ErrorBox";

const initialFormData: LoginRequest = {
    username: "",
    password: ""
}

export default function Login() {
    const {setUser} = useUser();
    const [errorMessage, setErrorMessage] = useState("");
    const [formData, setFormData] = useReducer(formReducer<LoginRequest>, initialFormData);
    const navigate = useNavigate();

    async function handleFormSubmit(event: FormEvent) {
        event.preventDefault();
        console.debug(formData);

        if (formData.password === "") {
            setErrorMessage("Password cannot be empty");
            return;
        }

        if (formData.username === "") {
            setErrorMessage("Username cannot be empty");
            return;
        }

        try {
            const user = await attemptLogin(formData);
            setUser(user);
            navigate("/user");
        } catch (e) {
            setErrorMessage("Incorrect username or password");
        }
    }

    return <div className={styles.loginContainer}>
        <h1>Login</h1>

        <form onSubmit={handleFormSubmit}>
            <label>
                <p>Nazwa użytkownika:</p>
                <input name="username" type="text" onChange={setFormData}/>
            </label>
            <label>
                <p>Hasło:</p>
                <input name="password" type="password" onChange={setFormData}/>
            </label>
            <ErrorBox message={errorMessage}/>
            <input type="submit" value="Log In"/>
        </form>
    </div>
}
