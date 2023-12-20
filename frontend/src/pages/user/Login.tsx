import {FormEvent, useReducer} from "react";
import UserService, {LoginRequest} from "../../lib/User";
import useUser from "../../hooks/useUser";
import {Link, useNavigate} from "react-router-dom";
import {formReducer} from "../../lib/utils";
import styles from "./Login.module.css"

const initialFormData: LoginRequest = {
    username: "",
    password: ""
}
// TODO error handling
export default function Login() {
    const {setUser} = useUser();
    const [formData, setFormData] = useReducer(formReducer<LoginRequest>, initialFormData);
    const navigate = useNavigate();

    function handleFormSubmit(event: FormEvent) {
        event.preventDefault();
        console.log(formData);
        UserService.attemptLogin(formData)
            .then(user => {
                setUser(user)
                navigate("/user")
            });
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
            <input type="submit" value="Log In"/>
            <Link to={"/user/forgot-password"}>Nie pamiętam hasła</Link>
        </form>
    </div>
}
