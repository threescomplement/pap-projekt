import {Course} from "./Course";
import {Link} from "./utils";
import api from "./api";


export interface Review {
    authorUsername: string
    opinion: string
    easeRating: string
    interestRating: string
    engagementRating: string
    created: string
    _links: {
        self: Link
        user: Link
        comments: Link
        course: Link
    }
}

export interface ReviewRequest {
    text: string,
    easeRating: number
    interestRating: number
    engagementRating: number
}


async function fetchReviewsByCourse(course: Course): Promise<Review[]> {
    return api.get(course._links.reviews.href)
        .then(r => r.json())
        .then(json => json._embedded.reviews)
        .catch(e => console.log(e));
}

async function fetchReviewByCourseIdAndAuthor(courseId: string, authorUsername: string): Promise<Review> {
    return api.get(`/courses/${courseId}/reviews/${authorUsername}`)
        .then(r => r.json());
}

async function postReview(request: ReviewRequest, courseId: string): Promise<void> {
    api.post(`/courses/${courseId}/reviews`, request)
        .catch(e => console.log(e));
}

async function deleteReview(courseId: string, username: string): Promise<boolean> {
    const response = await api.delete(`/courses/${courseId}/reviews/${username}`);
    console.log(response);
    return response.ok;
}

export const ReviewService = {
    fetchReviewsByCourse,
    fetchReviewByCourseIdAndAuthor,
    postReview,
    deleteReview
}