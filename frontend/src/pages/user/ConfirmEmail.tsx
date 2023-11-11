import {useNavigate, useParams} from "react-router-dom";
import {verifyEmail} from "../../lib/User";

/**
 * Confirm user's email address and redirect to login page
 *
 * Confirmation emails sent by the backend app link to this page
 *
 * @constructor
 */
export default function ConfirmEmail() {
    const {token} = useParams();
    const navigate = useNavigate();

    if (token == null) {
        navigate("/");
    }

    function handleConfirm() {
        verifyEmail(token!)
            .then(() => navigate("/user/login"))
    }

    return <>
        <h1>Confirm your email address</h1>
        <button onClick={handleConfirm}>Confirm</button>
    </>;
}