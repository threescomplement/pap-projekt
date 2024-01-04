import {Review} from "../lib/Review";
import React, {useEffect, useState, useCallback} from "react";
import {CommentRequest, CommentService, ReviewComment} from "../lib/ReviewComment";
import {ReviewCardWithoutLink} from "./ReviewCards";
import "./ReviewDetails.css"
import {useParams, useNavigate} from "react-router-dom";
import {EditBar} from "./EditBar";
import useUser from "../hooks/useUser";
import {User} from "../lib/User";
import MessageBox from "./MessageBox";
import ErrorBox from "./ErrorBox";

interface ReviewDetailsProps {
    review: Review
}

export function ReviewDetails({review}: ReviewDetailsProps) {
    const {courseId, authorUsername} = useParams();
    const [comments, setComments] = useState<ReviewComment[]>([]);
    const [newComment, setNewComment] = useState<string>("");
    const [message, setMessage] = useState<string>("");
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

    function afterDeletingReview() {
        navigate("/courses/" + courseId + "/reviewDeleted")
    }

    function afterDeletingComment() {
        reloadComments();
        setMessage("Komentarz został usunięty.")
    }

    return <div>
        <ReviewCardWithoutLink review={review} afterDeleting={afterDeletingReview}/>
        <MessageBox message={message}/>
        <CommentList comments={comments} afterDeleting={afterDeletingComment}/>
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
    afterDeleting: Function
}

export function CommentList({comments, afterDeleting}: CommentListProps) {
    return <ul>
        {comments //todo: sort
            .map(c => <li
                key={c.id}><CommentCard comment={c} afterDeleting={afterDeleting}/>
            </li>)}
    </ul>
}

interface CommentCardProps {
    comment: ReviewComment;
    afterDeleting: Function;
}

function CommentCard({comment, afterDeleting}: CommentCardProps) {
    const user: User = useUser().user!
    const isAdmin = user.roles[0] === "ROLE_ADMIN";
    const isCommentAuthor = user.username === comment.authorUsername;
    const [errorMessage, setErrorMessage] = useState<string>("");
    const modificationContent = (isAdmin || isCommentAuthor) ?
        <EditBar handleDelete={createCommentDeleteHandler(comment.id, afterDeleting, setErrorMessage)}
                 deleteConfirmationQuery={"Czy na pewno chcesz usunąć komentarz?"}/> : null;

    return <>

        <div>{comment.authorUsername} {modificationContent}</div>
        <div>{comment.text}</div>
        <ErrorBox message={errorMessage}/>
    </>
}


function createCommentDeleteHandler(commentId: string, afterDeleting: Function, errorBoxSetter: Function): React.MouseEventHandler {
    return async event => {
        event.preventDefault()
        CommentService.deleteComment(commentId)
            .then(deleted => {
                deleted ? afterDeleting() : errorBoxSetter("'Przy usuwaniu opinii wystąpił błąd. " +
                    "Spróbuj ponownie lub skontaktuj się z administracją...");
            })
    }
}