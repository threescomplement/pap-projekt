import {FormEvent, useReducer} from "react";
import {attemptLogin, LoginRequest} from "../../lib/User";
import useUser from "../../hooks/useUser";
import {useNavigate} from "react-router-dom";
import {formReducer} from "../../lib/utils";

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
        attemptLogin(formData)
            .then(user => {
                setUser(user)
                navigate("/user")
            });
    }

    return <>
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
        </form>
    </>
}
