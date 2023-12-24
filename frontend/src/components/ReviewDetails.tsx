import {Review} from "../lib/Review";
import React, {useEffect, useState, useCallback} from "react";
import {CommentRequest, CommentService, ReviewComment} from "../lib/ReviewComment";
import {ReviewCardWithoutLink} from "./ReviewCards";
import "./ReviewDetails.css"
import {useParams} from "react-router-dom";

interface ReviewDetailsProps {
    review: Review
}

export function ReviewDetails({review}: ReviewDetailsProps) {
    const {courseId, authorUsername} = useParams();
    const [comments, setComments] = useState<ReviewComment[]>([]);
    const [newComment, setNewComment] = useState<string>("");
    const memorizedReloadComments = useCallback(reloadComments, [review])

    useEffect(() => {
        memorizedReloadComments()
    }, [review, courseId, authorUsername, memorizedReloadComments]);

    function reloadComments() {
        CommentService.fetchCommentsByReview(review)
            .then(c => {
                setComments(c);
            })
            .catch(e => console.log(e));
    }

    function handleCommentSubmit() {
        if (newComment === "") return; //todo: inform user comment can't be blank
        const request: CommentRequest = {
            text: newComment
        }

        CommentService.postComment(request, courseId!, authorUsername!)
            .then(() => {
                reloadComments();
                setNewComment("")
            })
            .catch(e => console.log(e));
    }

    return <div>
        <ReviewCardWithoutLink review={review} refreshParent={reloadComments}/>
        <CommentList comments={comments}/>
        <div className="add-comment-container">
            <textarea
                placeholder="Twój komentarz"
                onChange={e => setNewComment(e.target.value)}
                value={newComment}
            />
            <button onClick={handleCommentSubmit}>Dodaj komentarz</button>
        </div>
    </div>
}

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
