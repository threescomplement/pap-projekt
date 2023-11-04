import {decodeToken} from "react-jwt";

export interface LoginRequest {
    username: string,
    password: string,
}

export interface User extends LoginRequest {
    id: number
    token: string,
    roles: string[],
}

interface AccessToken {
    sub: string,
    exp: number,
    u: string,
    a: string[]
}

export async function attemptLogin(loginRequest: LoginRequest): Promise<User> {
    const response = await fetch(`${process.env.REACT_APP_API_ROOT}auth/login`, {
        method: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(loginRequest),
    });
    const json = await response.json();
    const token = json.accessToken as string;
    const decodedToken = decodeToken(token) as AccessToken;
    return {
        id: Number.parseInt(decodedToken.sub),
        ...loginRequest,
        token: token,
        roles: decodedToken.a
    };
}
