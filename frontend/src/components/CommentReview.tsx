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
        <div>{'overallRating' in data._links && "This is a review"}</div>
    </>
}





//todo: edit, delete, etc based on the user logged in