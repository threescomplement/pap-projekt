import {useEffect, useState} from "react";
import {useJwt} from "react-jwt";
import {attemptLogin, LoginRequest, User} from "../lib/User";

export function Login() {
    const loginRequest: LoginRequest = {username: "rdeckard", password: "password"}
    const [user, setUser] = useState<User | null>(null);
    const [isLoaded, setIsLoaded] = useState(false);

    useEffect(() => {
        attemptLogin(loginRequest)
            .then(u => {
                setUser(u)
                setIsLoaded(true)
            })
    }, [])

    return <>
        <h1>Login</h1>
        {isLoaded ? <Welcome user={user!}/> : <p>Loading...</p>}
    </>;
}


export function Welcome({user}: { user: User }) {
    console.log(user);
    const {decodedToken, isExpired} = useJwt(user.token!);
    return <h1>
        <p>Welcome {user.username}</p>
        <p>Your token is {isExpired ? "expired" : "valid"}</p>
        <p>{JSON.stringify(decodedToken)}</p>
    </h1>
}
