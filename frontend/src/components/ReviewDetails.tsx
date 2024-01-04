import {Review} from "../lib/Review";
import React, {useEffect, useState, useCallback} from "react";
import {CommentRequest, CommentService, ReviewComment} from "../lib/ReviewComment";
import {ReviewCardWithoutLink} from "./ReviewCards";
import "./ReviewDetails.css"
import {useParams, useNavigate} from "react-router-dom";
import {EditBar} from "./EditBar";
import useUser from "../hooks/useUser";
import {User} from "../lib/User";

interface ReviewDetailsProps {
    review: Review
}

export function ReviewDetails({review}: ReviewDetailsProps) {
    const {courseId, authorUsername} = useParams();
    const [comments, setComments] = useState<ReviewComment[]>([]);
    const [newComment, setNewComment] = useState<string>("");
    const memorizedReloadComments = useCallback(reloadComments, [review]);
    const navigate = useNavigate();

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

    function afterDeletingReview() {navigate("/courses/" + courseId + "/reviewDeleted")}

    return <div>
        <ReviewCardWithoutLink review={review} afterDeleting={afterDeletingReview}/>
        <CommentList comments={comments} refreshParent={reloadComments}/>
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
    comments: ReviewComment[],
    refreshParent: Function
}

export function CommentList({comments, refreshParent}: CommentListProps) {
    return <ul>
        {comments //todo: sort
            .map(c => <li
                key={c.id}><CommentCard comment={c} refreshParent={refreshParent}/>
            </li>)}
    </ul>
}

interface CommentCardProps {
    comment: ReviewComment;
    refreshParent: Function;
}

function CommentCard({comment, refreshParent}: CommentCardProps) {
    const user: User = useUser().user!
    const isAdmin = user.roles[0] === "ROLE_ADMIN";
    const isCommentAuthor = user.username === comment.authorUsername;
    const modificationContent = (isAdmin || isCommentAuthor) ?
        <EditBar handleDelete={createCommentDeleteHandler(comment.id, refreshParent)}
                 deleteConfirmationQuery={"Czy na pewno chcesz usunąć komentarz?"}/> : null;

    return <>
        <div>{comment.authorUsername} {modificationContent}</div>
        <div>{comment.text}</div>
    </>
}


function createCommentDeleteHandler(commentId: string, afterDeleting: Function): React.MouseEventHandler {
    return async event => {
        event.preventDefault()
        CommentService.deleteComment(commentId)
            .then(deleted => {
                //todo: should we even display the feedback? is it frustrating to click through the popups?
                let feedback = deleted ? 'Comment deleted successfully!' : 'Failed to delete comment! Please try again...';
                alert(feedback);
                afterDeleting();
            })
    }
}