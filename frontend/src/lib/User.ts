import {decodeToken} from "react-jwt";
import {defaultHeaders} from "./utils";

export interface User {
    id: number,
    username: string,
    email: string,
    password: string,
    token: string,
    roles: string[],
}

interface AccessToken {
    sub: string,
    exp: number,
    u: string,
    e: string,
    a: string[]
}

export interface LoginRequest {
    username: string,
    password: string,
}

export interface RegisterRequest {
    username: string,
    email: string,
    password: string,
}

/**
 * Log in user - acquire JWT token
 * @param loginRequest - login credentials
 */
export async function attemptLogin(loginRequest: LoginRequest): Promise<User> {
    const response = await fetch(`${process.env.REACT_APP_API_ROOT}auth/login`, {
        method: "POST",
        headers: defaultHeaders,
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

/**
 * Register new user
 *
 * @param request - credentials of new user
 */
export async function attemptRegister(request: RegisterRequest): Promise<User> {
    const response = await fetch(`${process.env.REACT_APP_API_ROOT}users`, {
        method: "POST",
        headers: defaultHeaders,
        body: JSON.stringify(request),
    })

    return await response.json() as User;
}

/**
 * Confirm user's email address
 * @param emailVerificationToken
 */
export async function verifyEmail(emailVerificationToken: string): Promise<User> {
    const response = await fetch(`${process.env.REACT_APP_API_ROOT}users/verify`, {
        method: "POST",
        headers: defaultHeaders,
        body: JSON.stringify({token: emailVerificationToken})
    });
    return await response.json() as User;
}
