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

export const NUM_REVIEWS_PLACEHOLDER = 0;

export function ratingToPercentage(rating: string | number) {
    const numericRating = typeof rating === 'string' ? parseFloat(rating) : rating;
    if (isNaN(numericRating)) return '0%';
    return `${Math.floor(numericRating * 10)}%`
}