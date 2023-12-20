// Miscellaneous utilities

import {ChangeEvent} from "react";
import {Review} from "./Review";
import {ReviewComment} from "./ReviewComment";


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

export function getDummyReviews(): Review[] {
    return [{
        authorUsername: "gordonsysy123",
        opinion: "Wyczumpisty kurs!",
        overallRating: "9",
        created: "2023-12-06T22:51:36.585+00:00",
        _links: null
    }, {
        authorUsername: "czumpi94ez",
        opinion: "Średnio na jeża",
        overallRating: "4",
        created: "2023-12-06T22:50:36.585+00:00",
        _links: null
    }]
}

export function getDummyComments(): ReviewComment[] {
    return [{
        id: "1",
        authorUsername: "waltuh",
        text: "Nie zgadzam się!",
        created: "2023-12-06T22:55:36.585+00:00",
        _links: null
    }, {
        id: "2",
        authorUsername: "jesser",
        text: "Zgadzam się!",
        created: "2023-12-06T22:56:36.585+00:00",
        _links: null
    }]
}