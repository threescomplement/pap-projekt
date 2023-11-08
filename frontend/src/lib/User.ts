import {decodeToken} from "react-jwt";

export interface LoginRequest {
    username: string,
    password: string,
}

export interface User extends LoginRequest {
    id: number,
    email: string,
    token: string,
    roles: string[],
}

export interface RegisterRequest extends LoginRequest {
    email: string
}

interface AccessToken {
    sub: string,
    exp: number,
    u: string,
    e: string,
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
        email: decodedToken.e,
        ...loginRequest,
        token: token,
        roles: decodedToken.a
    };
}

export async function attemptRegister(request: RegisterRequest): Promise<User> {
    const response = await fetch(`${process.env.REACT_APP_API_ROOT}users`, {
        method: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(request),
    })

    return await response.json() as User;
}

export async function verifyEmail(token: string): Promise<User> {
    const response = await fetch(`${process.env.REACT_APP_API_ROOT}users/verify`, {
        method: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({token: token})
    });
    return await response.json() as User;
}
