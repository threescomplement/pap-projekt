import {Course} from "./Course";
import {getDummyReviews} from "./utils";
import api from "./api";
import {ReviewComment} from "./ReviewComment";


export interface Review {
    id: string;
    authorUsername: string;
    opinion: string;
    created: string
    overallRating: string;
    _links: any;
}


async function fetchReviewsByCourse(course: Course): Promise<Review[]> {
    return api.get(course._links.reviews.href)
        .then(r => r.json())
        .then(json => json._embedded.reviews)
        .catch(e => console.log(e));
}

async function fetchReviewByCourseIdAndAuthor(courseId: string, authorUsername: string): Promise<Review> {
    return api.get((process.env.REACT_APP_API_ROOT + "/courses/" + courseId + "/reviews/" + authorUsername))
        .then(r=>r.json());
}

async function fetchReviewsByTeacher(): Promise<Review[]> {
    return getDummyReviews();
}

async function fetchReviewsByUser(): Promise<Review[]> {
    return getDummyReviews();
}

export const ReviewService = {
    fetchReviewsByCourse,
    fetchReviewByCourseIdAndAuthor,
    fetchReviewsByTeacher,
    fetchReviewsByUser
}