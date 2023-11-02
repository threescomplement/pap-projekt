import {useEffect, useState} from "react";
import { useJwt } from "react-jwt";

export function Login() {
    const user = {
        username: "rdeckard",
        password: "password"
    }
    const [token, setToken] = useState<string>("");
    const [isLoaded, setIsLoaded] = useState(false);

    const {decodedToken, isExpired} = useJwt(token);

    useEffect(() => {
        fetch("http://localhost:8080/api/auth/login", {
            method: "POST",
            body: JSON.stringify(user),
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
        })
            .then(response => response.json())
            .then(json => {
                console.log(json)
                setToken(json.accessToken)
                setIsLoaded(true)
            })
            .catch(e => console.error(e))
    })


    return <>
        <h1>Login</h1>
        <p>{isLoaded ? token : "Loading..."}</p>
        {isLoaded ? <p>{JSON.stringify(decodedToken)}</p> : null}
        <p>{isExpired ? "Expired" : "Valid"}</p>
    </>;
}
