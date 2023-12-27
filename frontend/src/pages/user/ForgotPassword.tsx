import {FormEvent, useState} from "react";
import MessageBox from "../../components/MessageBox";
import UserService from "../../lib/User";
import {Link} from "react-router-dom";

export default function ForgotPassword() {
    const [email, setEmail] = useState("");
    const [errorMessage, setErrorMessage] = useState("");
    const [emailSent, setEmailSent] = useState(false);

    async function handleSubmit(event: FormEvent) {
        event.preventDefault();
        if (email === "") {
            setErrorMessage("Please enter email");
            return;
        }

        try {
            const success = await UserService.sendResetPasswordEmail(email);
            if (!success) {
                setErrorMessage("Nie istnieje użytkownik o podanym adresie email");
                return;
            }
            setEmailSent(true);
        } catch (e) {
            setErrorMessage("Operacja nie powiodła się");
        }
    }

    if (emailSent) {
        return <div>
            <h1>Odzyskiwanie hasła</h1>
            <MessageBox message={"Wysłano link do zmiany hasła na podany adres"}/>
            <button>
                <Link to={"/user/login"}>Zaloguj się</Link>
            </button>
        </div>
    }

    return <div>
        <form onSubmit={e => handleSubmit(e)}>
            <label>
                <p>Email:</p>
                <input type="email" onChange={e => setEmail(e.target.value)}/>
            </label>
            {/*<ErrorBox message={errorMessage}/>*/}
            <input type="submit" value="Wyślij"/>
        </form>
    </div>
}