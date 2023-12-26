import {Course} from "./Course";
import {getDummyReviews} from "./utils";
import api from "./api";


export interface Review {
    authorUsername: string;
    opinion: string;
    overallRating: string;
    created: string
    _links: any;
}

export interface ReviewRequest {
    text: string,
    rating: number
}


async function fetchReviewsByCourse(course: Course): Promise<Review[]> {
    return api.get(course._links.reviews.href)
        .then(r => r.json())
        .then(json => json._embedded.reviews)
        .catch(e => console.log(e));
}

async function fetchReviewByCourseIdAndAuthor(courseId: string, authorUsername: string): Promise<Review | null> {
    const response = await api.get((process.env.REACT_APP_API_ROOT + "/courses/" + courseId + "/reviews/" + authorUsername))
    // todo: better error handling
    console.log(response)
    if (response.status === 404) {
        return null
    }
    return response.json()
}

async function postReview(request: ReviewRequest, courseId: string) {
    api.post((process.env.REACT_APP_API_ROOT + "/courses/" + courseId + "/reviews"), request)
        .catch(e => console.log(e));
}

async function deleteReview(courseId: string, username: string) {
    const response = await api.delete((process.env.REACT_APP_API_ROOT + "/courses/" + courseId + "/reviews/" + username));
    console.log(response);
    return response.ok;
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
    postReview,
    deleteReview,
    fetchReviewsByTeacher,
    fetchReviewsByUser
}