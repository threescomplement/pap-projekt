// Miscellaneous utilities

import {ChangeEvent} from "react";


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

