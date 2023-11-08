import {User} from "../lib/User";
import useUser from "../hooks/useUser";
import {useNavigate} from "react-router-dom";


interface ProfileProps {
    user: User | null
}
export default function Profile({user}: ProfileProps) {
    const {setUser} = useUser();
    const navigate = useNavigate();

    if (user == null) {
        return <h1>Log in to view your profile</h1>;
    }

    const handleLogout = () => {
        setUser(null);
        navigate("/user/login");
    }

    return <>
        <h1>My profile</h1>
        <p>Username: {user.username}</p>
        <p>Email: {user.email}</p>
        <p>Roles: {user.roles}</p>
        <button onClick={handleLogout}>Log Out</button>
    </>;
}