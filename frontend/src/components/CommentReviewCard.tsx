import {ReviewComment} from "../lib/ReviewComment";
import React from "react";
import {Link} from "react-router-dom";


interface CommentReviewCardProps {
    data: ReviewComment;
}

export function CommentReviewCard({data}: CommentReviewCardProps) {
    return <>
        <div>{data.authorUsername}</div>
        <div>{'overallRating' in data && "Ocena: " + data.overallRating}</div>
        <div>{data.opinion}</div>
        <div>
            {'overallRating' in data &&
                <Link to={"comments/" + data.authorUsername}> Czytaj więcej </Link>}
                </div>
                </>
            }


            //todo: edit, delete, etc based on the user logged in