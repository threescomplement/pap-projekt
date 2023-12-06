import {Course} from "./Course";
import {getDummyReviews} from "./utils";
import api from "./api";


export interface Review {
    id: string;
    username: string;
    opinion: string;
    overallRating: string;
    _links: any;
}


async function fetchReviewsByCourse(course: Course) {
    console.log(course._links.reviews.href)
    return await api.get(course._links.reviews.href)
        .then(r => r.json())
        .then(json => json._embedded.reviews)
        .catch(e => console.log(e));
}

async function fetchReviewsByTeacher() {
    return getDummyReviews();
}

async function fetchReviewsByUser() {
    return getDummyReviews();
}

export const ReviewService = {
    fetchReviewsByCourse,
    fetchReviewsByTeacher,
    fetchReviewsByUser
}