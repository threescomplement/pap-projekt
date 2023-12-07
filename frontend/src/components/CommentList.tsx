import {ReviewComment} from "../lib/ReviewComment";
import React from "react";

interface CommentListProps {
    comments: ReviewComment[]
}

export function CommentList({comments}: CommentListProps) {
    return <ul>
        {comments
            .map(c => <li
                id={c.id}><CommentCard data={c}/>
            </li>)}
    </ul>
}

interface CommentCardProps {
    data: ReviewComment;
}

function CommentCard({data}: CommentCardProps) {
    return <>
        <div>{data.authorUsername}</div>
        <div>{data.text}</div>
    </>
}
