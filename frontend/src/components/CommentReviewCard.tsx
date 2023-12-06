import {ReviewComment} from "../lib/ReviewComment";


interface CommentReviewCardProps {
    data: ReviewComment;
}

export function CommentReviewCard({data}: CommentReviewCardProps) {
    return <>
        <div>{data.authorUsername}</div>
        <div>{'overallRating' in data && "Ocena: " + data.overallRating}</div>
        <div>{data.opinion}</div>
        <div>{'overallRating' in data._links && "This is a review"}</div>
    </>
}





//todo: edit, delete, etc based on the user logged in