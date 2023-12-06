import {Review} from "../lib/Review";
import React, {useEffect, useState} from "react";
import {CommentService, ReviewComment} from "../lib/ReviewComment";
import {CommentReviewCard} from "./CommentReview";
import {CommentList} from "./CommentList";

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
