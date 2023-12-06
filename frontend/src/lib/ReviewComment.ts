import {Review} from "./Review";
import api from "./api";


export interface ReviewComment {
    id: string;
    opinion: string;
    authorUsername: string,
    _links: any;
}

async function fetchCommentsByReview(review: Review) {
    return await api.get(review._links.comments.href)
        .then(c => c.json())
        .then(json => json._embedded.comments)
        .catch(e => console.log(e));
}


export const CommentService = {
    fetchCommentsByReview
}