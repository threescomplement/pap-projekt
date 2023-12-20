import {useParams} from "react-router-dom";

export default function ResetPassword() {
    const {token} = useParams();

    return <div>
        <h1>Odzyskiwanie hasła</h1>
        {/*TODO*/}
    </div>;
}