import {Review} from "../lib/Review";
import {CommentService, ReviewComment} from "../lib/ReviewComment";
import {useEffect, useState} from "react";


interface CommentReviewCardProps {
    data: ReviewComment | Review;
}

export function CommentReviewCard({data}: CommentReviewCardProps) {
    return <>
        <div>{data.username}</div>
        <div>{'overallRating' in data && "Ocena: " + data.overallRating}</div>
        <div>{data.opinion}</div>
    </>
}


interface ReviewDetailsProps {
    review: Review
}

export function ReviewDetails({review}: ReviewDetailsProps) {
    const [comments, setComments] = useState<ReviewComment[]>([]);

    useEffect(() => {
        CommentService.fetchCommentsByReview(review)
            .then(c => {
                setComments(c)
            })
    }, [review]);

    return <div>
        <CommentReviewCard data={review}/>
        <CommentList comments={comments}/>
    </div>
}

interface CommentListProps {
    comments: ReviewComment[]
}

function CommentList({comments}: CommentListProps) {
    return <div>
        {comments.map(c => <CommentReviewCard data={c}/>)}
    </div>
}

//todo: edit, delete, etc based on the user logged in