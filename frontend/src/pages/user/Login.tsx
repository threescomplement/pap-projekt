import {FormEvent, useReducer, useState} from "react";
import UserService, {LoginRequest} from "../../lib/User";
import useUser from "../../hooks/useUser";
import {Link, useNavigate} from "react-router-dom";
import {formReducer} from "../../lib/utils";
import styles from "../../ui/components/Forms.module.css"
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
            setErrorMessage("Hasło nie może być puste");  //TODO do not allow weak passwords
            return;
        }

        if (formData.username === "") {
            setErrorMessage("Nazwa użytkownika nie może być pusta");
            return;
        }

        try {
            const user = await UserService.attemptLogin(formData);
            setUser(user);
            navigate("/user");
        } catch (e) {
            setErrorMessage("Nieprawidłowy login lub hasło");
        }
    }

    return <div className={styles.formContainer}>
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
            <div className={styles.buttonContainer}>
            <input type="submit" value="Log In"/>
            <Link className={styles.forgotPasswordLink} to={"/user/forgot-password"}>Nie pamiętam hasła</Link>
            </div>
        </form>
    </div>
}
