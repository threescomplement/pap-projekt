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

export function commonElements<T>(arrays: T[][]): T[] {
    if (arrays.length === 0) {
        return [];
    }
    const baseArray = arrays[0];

    return baseArray.filter(element =>
        arrays.every(array => array.some(item => JSON.stringify(item) === JSON.stringify(element)))
    );
}

export const NUM_REVIEWS_PLACEHOLDER = 0;
export const COURSE_TEACHER_PLACEHOLDER = "dr. Andrzej Sysy";