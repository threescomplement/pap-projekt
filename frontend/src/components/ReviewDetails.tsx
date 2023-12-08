import {Review} from "../lib/Review";
import React, {useEffect, useState} from "react";
import {CommentRequest, CommentService, ReviewComment} from "../lib/ReviewComment";
import {CommentList} from "./CommentList";
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

    function reloadCourses() {
        CommentService.fetchCommentsByReview(review)
            .then(c => {
                console.log(comments)
                setComments(c)
            })
    }

    function handleCommentSubmit() {
        if (newComment == "") return; //todo: inform user comment can't be blank
        const request: CommentRequest = {
            text: newComment
        }
        CommentService.postComment(request, courseId!, authorUsername!)
            .then(_ => {
                reloadCourses();
                setNewComment("")
            })
    }

    reloadCourses();
    return <div>
        <ReviewCardWithoutLink review={review}/>
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
