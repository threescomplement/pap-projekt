import {ChangeEvent, FormEvent, useReducer} from "react";
import {attemptRegister, RegisterRequest} from "../lib/User";
import {useNavigate} from "react-router-dom";
import {formReducer} from "../lib/utils";

const initialFormData: RegisterRequest = {
    username: "",
    email: "",
    password: ""
}

export default function Register() {
    const [formData, setFormData] = useReducer(formReducer<RegisterRequest>, initialFormData);
    const navigate = useNavigate();

    function handleFormSubmit(event: FormEvent) {
        event.preventDefault();
        console.log(formData);
        attemptRegister(formData)
            .then(() => navigate("/user/login"));  // TODO: show message to check the mail inbox instead of redirecting to login page
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
