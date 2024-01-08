import {Link, Outlet} from "react-router-dom";
import styles from './Layout.module.css'
import useUser from "../hooks/useUser";

/**
 * Define the layout of the page
 *
 * The `<Outlet/>` component will be replaced with actual page content by React Router
 *
 * @constructor
 */
export function Layout() {
    const {user} = useUser();

    return <>
        <nav className={styles.navbarNav}>
            <ul className={styles.navbarList}>
                <li className={styles.navbarLink}>
                    <Link to="/">Home</Link>
                </li>
                {user != null && <>
                    <li className={styles.navbarLink}>
                        <Link to="/courses">Kursy</Link>
                    </li>
                    <li className={styles.navbarLink}>
                        <Link to="/teachers">Lektorzy</Link>
                    </li>
                </>}
                {user == null ? <>
                    <li className={styles.navbarLink}>
                        <Link to="/user/login">Login</Link>
                    </li>
                    <li className={styles.navbarLink}>
                        <Link to="/user/register">Rejestracja</Link>
                    </li>
                </> : <li className={styles.profileLink}>Welcome <Link to="/user">{user.username}</Link></li>
                }
                {user?.roles[0] === "ROLE_ADMIN" ? <li className={styles.navbarLink}>
                    <Link to="/admin">Panel administratora</Link>
                </li> : null}
            </ul>
        </nav>
        <Outlet/>
    </>
}