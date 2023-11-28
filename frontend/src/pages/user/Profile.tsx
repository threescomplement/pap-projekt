import {User} from "../../lib/User";
import useUser from "../../hooks/useUser";
import {useNavigate} from "react-router-dom";


interface ProfileProps {
    user: User | null
}
export default function Profile({user}: ProfileProps) {
    const {setUser} = useUser();
    const navigate = useNavigate();

    if (user == null) {
        return <h1>Zaloguj się, aby wyświetlić swój profil...</h1>;
    }

    const handleLogout = () => {
        setUser(null);
        navigate("/user/login");
    }

    return <>
        <h1>Profil</h1>
        <p>Nazwa użytkownika: {user.username}</p>
        <p>Email: {user.email}</p>
        <p>Role: {user.roles}</p>
        <button onClick={handleLogout}>Wyloguj się</button>
    </>;
}