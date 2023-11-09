import {Link, Outlet} from "react-router-dom";
import "./Layout.css"
import useUser from "../hooks/useUser";

export function Layout() {
    const {user} = useUser();

    return <>
        <nav className="navbar-nav">
            <ul className="navbar-list">
                <li className="navbar-link">
                    <Link to="/">Home</Link>
                </li>
                <li className="navbar-link">
                    <Link to="/courses">Courses</Link>
                </li>
                <li className="navbar-link">
                    <Link to="/teachers">Teachers</Link>
                </li>
                {user == null ? <>
                    <li className="navbar-link">
                        <Link to="/user/login">Login</Link>
                    </li>
                    <li className="navbar-link">
                        <Link to="/user/register">Register</Link>
                    </li>
                </> : <p>Welcome {user.username}</p>
                }
            </ul>
        </nav>
        <Outlet/>
    </>
}