import {Link, Outlet} from "react-router-dom";

export function Layout() {
    return <>
        <nav>
            <ul>
                <li>
                    <Link to="/">Home</Link>
                </li>
                <li>
                    <Link to="/login">Login</Link>
                </li>
                <li>
                    <Link to="/courses">Courses</Link>
                </li>
                <li>
                    <Link to="/teachers">Teachers</Link>
                </li>
            </ul>
        </nav>
        <Outlet/>
    </>
}