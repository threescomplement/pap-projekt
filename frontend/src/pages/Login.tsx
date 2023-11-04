import {useEffect} from "react";
import {decodeToken, isExpired} from "react-jwt";
import {attemptLogin, LoginRequest, User} from "../lib/User";
import useUser from "../hooks/useUser";

export function Login() {
    const loginRequest: LoginRequest = {username: "rdeckard", password: "password"}
    const {user, setUser} = useUser();

    useEffect(() => {
        if (user == null) {
            attemptLogin(loginRequest)
                .then(u => setUser(u));
        }
    }, [])

    return <>
        <h1>Login</h1>
        {user != null ? <Welcome user={user!}/> : <p>Loading...</p>}
    </>;
}


export function Welcome({user}: { user: User }) {
    console.log(user);
    return <h1>
        <p>Welcome {user.username}</p>
        <p>Your token is {isExpired(user.token) ? "expired" : "valid"}</p>
        <p>{JSON.stringify(decodeToken(user.token))}</p>
        <p>Your roles are:</p>
        <ul>
            {user.roles.map(r => <li key={r}>{r}</li>)}
        </ul>
    </h1>
}
