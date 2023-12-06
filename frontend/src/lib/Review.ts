import {Course} from "./Course";
import {getDummyReviews} from "./utils";


export interface Review {
    id: string;
    username: string;
    opinion: string;
    overallRating: string;
    _links: any;
}


async function fetchReviewsByCourse(course: Course) {
    return getDummyReviews();
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