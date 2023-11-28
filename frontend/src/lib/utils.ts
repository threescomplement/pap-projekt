// Miscellaneous utilities

import {ChangeEvent} from "react";
import {User} from "./User";


/**
 * Generic reducer for handling inputs in a form
 *
 * @param state - all values entered in the form
 * @param event - change event of a form input
 */
export function formReducer<T>(state: T, event: ChangeEvent<HTMLInputElement>): T {
    return {
        ...state,
        [event.target.name]: event.target.value
    }
}

export function authHeader(user: User) {
    return {Authorization: `Bearer ${user.token}`}
}

export const defaultHeaders = {
    'Accept': 'application/json',
    'Content-Type': 'application/json'
};