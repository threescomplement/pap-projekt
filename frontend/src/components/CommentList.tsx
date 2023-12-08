import {ReviewComment} from "../lib/ReviewComment";
import React from "react";

interface CommentListProps {
    comments: ReviewComment[]
}

export function CommentList({comments}: CommentListProps) {
    return <ul>
        {comments //todo: sort
            .map(c => <li
                key={c.id}><CommentCard review={c}/>
            </li>)}
    </ul>
}

interface CommentCardProps {
    review: ReviewComment;
}

function CommentCard({review}: CommentCardProps) {
    return <>
        <div>{review.authorUsername}</div>
        <div>{review.text}</div>
    </>
}
