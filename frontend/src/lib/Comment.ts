

export interface Comment {
    id: string;
    opinion: string;
    username: string,
    _links: any;
}

async function fetchCommentsByReview() {}


export const CommentService = {
    fetchCommentsByReview
}