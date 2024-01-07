import {Review} from "./Review";
import api from "./api";
import {Link} from "./utils";


export interface ReviewComment {
    id: string;
    text: string;
    authorUsername: string,
    created: string
    _links: {
        self: Link
        review: Link
        user: Link
    }
}

export interface CommentRequest {
    text: string
}

async function fetchCommentsByReview(review: Review): Promise<ReviewComment[]> {
    return api.get(review._links.comments.href)
        .then(c => c.json())
        .then(json => json._embedded.comments)
        .catch(e => console.log(e));
}

async function postComment(request: CommentRequest, courseId: string, reviewAuthor: string) {
    try {
        await api.post(`/courses/${courseId}/reviews/${reviewAuthor}/comments`, request)
    } catch (e) {
        console.log(e)
    }
}

async function deleteComment(commentId: string) {
    const response = await api.delete(`/comments/${commentId}`);
    console.log(response);
    return response.ok;
}


export const CommentService = {
    fetchCommentsByReview,
    postComment,
    deleteComment
}