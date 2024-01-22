import {FormEvent, useReducer, useState} from "react";
import UserService, {RegisterRequest} from "../../lib/User";
import {useNavigate} from "react-router-dom";
import {formReducer} from "../../lib/utils";
import formStyles from "../../ui/components/Forms.module.css"
import "../../ui/index.css"

const initialFormData: RegisterRequest = {
    username: "",
    email: "",
    password: ""
}

export default function Register() {
    const [formData, setFormData] = useReducer(formReducer<RegisterRequest>, initialFormData);
    const [isRegistered, setIsRegistered] = useState(false);
    const navigate = useNavigate();

    function handleFormSubmit(event: FormEvent) {
        event.preventDefault();
        console.log(formData);
        UserService.attemptRegister(formData)
            .then(() => setIsRegistered(true));
    }

    if (isRegistered) {
        return (
            <div className="tw-max-w-.9 tw-my-10 tw-ml-20 tw-p-5">
                <h1 className="tw-mb-10 tw-mt-10 tw-text-5xl">Rejestracja ukończona</h1>
                <p className="tw-text-2xl">Potwierdź adres mailowy przed zalogowaniem</p>
                <button className="tw-py-3 tw-px-8 tw-text-xl tw-mt-6 tw-w-2/12"
                        onClick={() => navigate("/user/login")}>Login
                </button>
            </div>
        );

    }

    return <div className={formStyles.formContainer}>
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
            <input className={formStyles.registerButton} type="submit" value="Rejestracja"/>
        </form>
    </div>
}
