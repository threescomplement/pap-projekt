import {useNavigate, useParams} from "react-router-dom";
import {FormEvent, useReducer, useState} from "react";
import {formReducer} from "../../lib/utils";
import UserService from "../../lib/User";

const initialFormData = {
    password: "",
    confirmPassword: ""
}

interface ResetPasswordFormData {
    password: string,
    confirmPassword: string
}

export default function ResetPassword() {
    const navigate = useNavigate();
    const {token} = useParams();
    const [formData, setFormData] = useReducer(formReducer<ResetPasswordFormData>, initialFormData);
    const [errorMessage, setErrorMessage] = useState("");

    if (token == null) {
        return <div>
            <h1>Odzyskiwanie hasła</h1>
            <p>Nieprawidłowy link</p>
        </div>;
    }

    async function handleSubmit(e: FormEvent) {
        e.preventDefault();
        //TODO better UX
        if (formData.password !== formData.confirmPassword) {
            setErrorMessage("Passwords do not match");
            return;
        }

        const success = await UserService.resetPassword(formData.password, token!);
        if (success) {
            navigate("/user/login");
        } else {
            setErrorMessage("Unable to reset password, try again, the link may be expired");
        }

    }


    return <div>
        <h1>Odzyskiwanie hasła</h1>
        <div>
            <form onSubmit={handleSubmit}>
                <label>
                    <p>Nowe hasło: </p>
                    <input name="password" type="password" onChange={setFormData}/>
                </label>
                <label>
                    <p>Potwierdź nowe hasło</p>
                    <input name="confirmPassword" type="password" onChange={setFormData}/>
                </label>
                {/*<ErrorBox message={errorMessage}/> //TODO*/}
                <input type="submit" value="Resetuj hasło"/>
            </form>
        </div>
    </div>;
}