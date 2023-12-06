import {Review} from "../lib/Review";
import {Comment} from "../lib/Comment";


interface CommentReviewCardProps {
    data: Comment | Review;
}

export function CommentReviewCard({data}: CommentReviewCardProps) {
    return <>
    <div>{data.username}</div>
        <div>{'overallRating' in data &&  "Ocena: " + data.overallRating}</div>
        <div>{data.opinion}</div>
    </>
}