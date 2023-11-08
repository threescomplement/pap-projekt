import {ChangeEvent, FormEvent, useReducer, useState} from "react";
import {attemptRegister, RegisterRequest} from "../lib/User";
import {Link, useNavigate} from "react-router-dom";
import {formReducer} from "../lib/utils";

const initialFormData: RegisterRequest = {
    username: "",
    email: "",
    password: ""
}

export default function Register() {
    const [formData, setFormData] = useReducer(formReducer<RegisterRequest>, initialFormData);
    const navigate = useNavigate();
    const [isRegistered, setIsRegistered] = useState(false);

    function handleFormSubmit(event: FormEvent) {
        event.preventDefault();
        console.log(formData);
        attemptRegister(formData)
            .then(() => setIsRegistered(true));
    }

    if (isRegistered) {
        return <>
            <h1>Registration complete</h1>
            <p>Confirm your email before logging in</p>
            <Link to="/user/login">Log in</Link>
        </>;
    }

    return <>
        <h1>Register new account</h1>

        <form onSubmit={handleFormSubmit}>
            <label>
                <p>Username:</p>
                <input name="username" type="text" onChange={setFormData} />
            </label>
            <label>
                <p>Email:</p>
                <input name="email" type="email" onChange={setFormData} />
            </label>
            <label>
                <p>Password:</p>
                <input name="password" type="password" onChange={setFormData} />
            </label>
            <input type="submit" value="Register"/>
        </form>
    </>
}
