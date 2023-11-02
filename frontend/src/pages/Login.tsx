import {useContext, useEffect, useState} from "react";
import {useJwt} from "react-jwt";
import {attemptLogin, LoginRequest, User} from "../lib/User";
import {CurrentUserContext, ICurrentUserContext} from "../App";

export function Login() {
    const loginRequest: LoginRequest = {username: "rdeckard", password: "password"}
    const {user, setUser} = useContext(CurrentUserContext) as ICurrentUserContext;

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
    const {decodedToken, isExpired} = useJwt(user.token!);
    return <h1>
        <p>Welcome {user.username}</p>
        <p>Your token is {isExpired ? "expired" : "valid"}</p>
        <p>{JSON.stringify(decodedToken)}</p>
    </h1>
}
