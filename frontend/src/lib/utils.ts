import {RegisterRequest} from "./User";
import {ChangeEvent} from "react";

export function formReducer<T>(state: T, event: ChangeEvent<HTMLInputElement>): T {
    return {
        ...state,
        [event.target.name]: event.target.value
    }
}