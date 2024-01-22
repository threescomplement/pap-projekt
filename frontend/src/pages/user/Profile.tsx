import UserService, {User} from "../../lib/User";
import useUser from "../../hooks/useUser";
import {useNavigate} from "react-router-dom";
import styles from "../../ui/pages/Profile.module.css";
import {MdDeleteForever} from "react-icons/md";
import {useState} from "react";
import {ConfirmationPopup} from "../../components/ConfirmationPopup";

interface ProfileProps {
    user: User | null
}

export default function Profile({user}: ProfileProps) {
    const {setUser} = useUser();
    const navigate = useNavigate();
    const [showDeleteConfirmation, setShowDeleteConfirmation] = useState(false);

    if (user == null) {
        return <h1>Zaloguj się, aby wyświetlić swój profil...</h1>;
    }

    const roleString = user.roles[0] === "ROLE_ADMIN" ? "Administrator" :  "Użytkownik";

    const handleLogout = () => {
        setUser(null);
        navigate("/user/login");
    }

    const handleDeleteAccount = () => {
        UserService.deleteUser(user)
            .then(() => handleLogout())
            .catch(e => console.error(e))
    }

    return <div className={styles.wrapper}>
        <div className={styles.profileContainer}>
            <h1>Profil</h1>
            <div className={styles.userInfo}>
                <p>Nazwa użytkownika: {user.username}</p>
                <p>Email: {user.email}</p>
                <p>Rola: {roleString}</p>
            </div>
            <button className={styles.logoutButton} onClick={handleLogout}>Wyloguj się</button>
            <button onClick={() => navigate("/user/change-password")}>Zmień hasło</button>
            {showDeleteConfirmation
                ? <ConfirmationPopup query={"Czy na pewno chcesz usunąć swoje konto?"}
                                     handleConfirmation={handleDeleteAccount}
                                     setVisibility={setShowDeleteConfirmation}/>
                : <button className={styles.deleteAccountButton} onClick={() => setShowDeleteConfirmation(true)}>
                    Usuń konto <MdDeleteForever/>
                </button>
            }
        </div>
    </div>;
}