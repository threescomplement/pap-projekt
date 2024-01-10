import ErrorBox from "../../components/ErrorBox";
import {FormEvent, useReducer, useState} from "react";
import {formReducer} from "../../lib/utils";
import UserService from "../../lib/User";
import MessageBox from "../../components/MessageBox";
import {useNavigate} from "react-router-dom";
import styles from "../../ui/components/Forms.module.css";


interface FormData {
    oldPassword: string
    newPassword: string
    confirmNewPassword: string
}

const initialFormData: FormData = {
    oldPassword: "",
    newPassword: "",
    confirmNewPassword: ""
}

export default function ChangePassword() {
    const navigate = useNavigate();
    const [formData, setFormData] = useReducer(formReducer<FormData>, initialFormData);
    const [errorMessage, setErrorMessage] = useState("");
    const [message, setMessage] = useState("");

    async function handleSubmit(e: FormEvent) {
        e.preventDefault();
        if (formData.newPassword !== formData.confirmNewPassword) {
            setErrorMessage("Podano różne wartości");
            return;
        }

        try {
            const ok = await UserService.changePassword(formData.oldPassword, formData.newPassword);
            if (!ok) {
                setErrorMessage("Nieprawidłowe hasło");
                return;
            }

            setErrorMessage("");
            setMessage("Hasło zostało zmienione, za chwilę zostaniesz przekierowany");
            setTimeout(() => {
                navigate(`/user`);
            }, 2000);

        } catch (e) {
            console.error(e);
            setErrorMessage("Operacja nie powiodła się");
        }
    }

    return <div className={styles.formContainer}>
        <h1>Zmiana hasła</h1>
        <form onSubmit={handleSubmit}>
            <label>
                <p>Aktualne hasło:</p>
                <input name="oldPassword" type="password" onChange={setFormData}/>
            </label>
            <label>
                <p>Nowe hasło</p>
                <input name="newPassword" type="password" onChange={setFormData}/>
            </label>
            <label>
                <p>Potwierdź nowe hasło</p>
                <input name="confirmNewPassword" type="password" onChange={setFormData}/>
            </label>
            <ErrorBox message={errorMessage}/>
            <MessageBox message={message}/>
            <input type="submit" value="Zmień hasło"/>
        </form>
    </div>;
}