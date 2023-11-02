export interface LoginRequest {
    username: string,
    password: string,
}

export interface User extends LoginRequest {
    token: string,
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
    return {
        ...loginRequest,
        token: json.accessToken
    };
}
