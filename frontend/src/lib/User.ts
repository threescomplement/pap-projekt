import {decodeToken} from "react-jwt";

import api from "./api";

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

const USER_STORAGE_HANDLE = "user";

function storeUser(user: User | null) {
    localStorage.setItem(USER_STORAGE_HANDLE, JSON.stringify(user));
}

function getStoredUser(): User | null {
    const content = localStorage.getItem(USER_STORAGE_HANDLE);
    if (content == null) {
        return null;
    }

    return JSON.parse(content);
}

/**
 * Log in user - acquire JWT token
 * @param loginRequest - login credentials
 */
async function attemptLogin(loginRequest: LoginRequest): Promise<User> {
    const response = await api.post("/auth/login", loginRequest);
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
async function attemptRegister(request: RegisterRequest): Promise<User> {
    const response = await api.post("/users", request)
    return await response.json() as User;
}

/**
 * Confirm user's email address
 * @param emailVerificationToken
 */
async function verifyEmail(emailVerificationToken: string): Promise<User> {
    const response = await api.post("/users/verify", {token: emailVerificationToken});
    return await response.json() as User;
}

async function sendResetPasswordEmail(email: string): Promise<void> {
    //TODO
}

async function resetPassword(newPassword: string, resetPasswordToken: string): Promise<boolean> {
    //TODO
    return false;
}

const UserService = {
    storeUser,
    getStoredUser,
    attemptLogin,
    attemptRegister,
    verifyEmail,
    sendResetPasswordEmail,
    resetPassword
}

export default UserService;