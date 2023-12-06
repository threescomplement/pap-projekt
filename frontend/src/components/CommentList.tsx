import {ReviewComment} from "../lib/ReviewComment";
import {CommentReviewCard} from "./CommentReview";

interface CommentListProps {
    comments: ReviewComment[]
}

export function CommentList({comments}: CommentListProps) {
    return <div>
        {comments.map(c => <CommentReviewCard data={c}/>)}
    </div>
}
