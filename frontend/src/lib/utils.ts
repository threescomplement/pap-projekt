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

export const NUM_REVIEWS_PLACEHOLDER = 0;

export function getDummyReviews(): Review[] {
    return [{
        authorUsername: "gordonsysy123",
        opinion: "Wyczumpisty kurs!",
        easeRating: "9",
        interestRating: "8",
        engagementRating: "6",
        created: "2023-12-06T22:51:36.585+00:00",
        _links: null
    }, {
        authorUsername: "czumpi94ez",
        opinion: "Średnio na jeża",
        easeRating: "4",
        interestRating: "2",
        engagementRating: "3",
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

export function ratingToPercentage(rating: string | number) {
   const numericRating = typeof rating === 'string' ? parseFloat(rating) : rating;
   if (isNaN(numericRating)) return '0%';
   return `${Math.floor(numericRating * 10)}%`
}