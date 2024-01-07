import {decodeToken} from "react-jwt";

import api from "./api";
import {Link} from "./utils";

/**
 * The user logged in on the website
 */
export interface User {
    id: number,
    username: string,
    email: string,
    password: string,
    token: string,
    roles: string[],
}

/**
 * Represents user of the application for admin management functionality
 * TODO: these should not be separate classes
 */
export interface AppUser {
    id: number,
    username: string,
    email: string,
    role: string,
    enabled: boolean
    _links: {
        self: Link,
        comments: Link,
        reviews: Link,
    }
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

/**
 * Return bool whether sending email succeeded
 */
async function sendResetPasswordEmail(email: string): Promise<boolean> {
    const response = await api.post("/users/send-reset-email", {email: email});
    return response.ok;
}

/**
 * Return bool whether password reset succeeded
 */
async function resetPassword(newPassword: string, resetPasswordToken: string): Promise<boolean> {
    const response = await api.post("/users/reset-password", {
        newPassword: newPassword,
        passwordResetToken: resetPasswordToken
    })

    return response.ok;

}

async function getAllUsers(): Promise<AppUser[]> {
    const response = await api.get("/users");
    const json = await response.json();
    return json._embedded.users;
}

async function updateUser(user: AppUser): Promise<AppUser> {
    const response = await api.put(`/users/${user.username}`, user);
    return response.json();
}

async function deleteUser(user: AppUser): Promise<boolean> {
    const response = await api.delete(`/users/${user.username}`);
    return response.ok;
}

const UserService = {
    storeUser,
    getStoredUser,
    attemptLogin,
    attemptRegister,
    verifyEmail,
    sendResetPasswordEmail,
    resetPassword,
    getAllUsers,
    updateUser,
    deleteUser
}

export default UserService;