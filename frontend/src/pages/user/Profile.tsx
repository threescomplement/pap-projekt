import {User} from "../../lib/User";
import useUser from "../../hooks/useUser";
import {useNavigate} from "react-router-dom";
import styles from "./Profile.module.css";

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

    return <div className={styles.wrapper}>
        <div className={styles.profileContainer}>
            <h1>Profil</h1>
            <div className={styles.userInfo}>
                <p>Nazwa użytkownika: {user.username}</p>
                <p>Email: {user.email}</p>
                <p>Role: {user.roles}</p>
            </div>
            <button className={styles.logoutButton} onClick={handleLogout}>Wyloguj się</button>
        </div>
    </div>;
}