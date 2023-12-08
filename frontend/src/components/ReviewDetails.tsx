import {Review} from "../lib/Review";
import React, {useEffect, useState} from "react";
import {CommentService, ReviewComment} from "../lib/ReviewComment";
import {CommentList} from "./CommentList";
import {ReviewCardWithoutLink} from "./ReviewCards";
import "./ReviewDetails.css"
import {CommentForm} from "./CommentForm";

interface ReviewDetailsProps {
    review: Review
}

export function ReviewDetails({review}: ReviewDetailsProps) {
    const [comments, setComments] = useState<ReviewComment[]>([]);
    const [reloadCommentsFlag, setReloadCommentsFlag] = useState<boolean>(true);

    useEffect(() => {
        console.log("in useEffect, reloadFlag is ", reloadCommentsFlag)
        CommentService.fetchCommentsByReview(review)
            .then(c => {
                console.log(comments)
                setComments(c)
            })
        console.log(comments.at(comments.length-1))
    }, [review, reloadCommentsFlag]);


    return <div>
        <ReviewCardWithoutLink review={review}/>
        <CommentList comments={comments}/>
        <CommentForm reloadFlag={reloadCommentsFlag} reloadFlagSetter={setReloadCommentsFlag}/>
    </div>
}
