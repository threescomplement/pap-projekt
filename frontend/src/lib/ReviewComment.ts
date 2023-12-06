import {Review} from "./Review";
import {getDummyComments} from "./utils";


export interface ReviewComment {
    id: string;
    opinion: string;
    username: string,
    _links: any;
}

async function fetchCommentsByReview(review: Review) {
    return getDummyComments();
}


export const CommentService = {
    fetchCommentsByReview
}