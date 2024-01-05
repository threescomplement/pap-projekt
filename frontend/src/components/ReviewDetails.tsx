import {Review} from "../lib/Review";
import React, {useEffect, useState, useCallback} from "react";
import {CommentRequest, CommentService, ReviewComment} from "../lib/ReviewComment";
import {ReviewCardWithoutLink} from "./ReviewCards";
import "./ReviewDetails.css"
import {useParams, useNavigate} from "react-router-dom";
import useUser from "../hooks/useUser";
import {User} from "../lib/User";
import MessageBox from "./MessageBox";
import ErrorBox from "./ErrorBox";
import {EditBar} from "./EditBar";

interface ReviewDetailsProps {
    review: Review
}

export function ReviewDetails({review}: ReviewDetailsProps) {
    const {courseId, authorUsername} = useParams();
    const [comments, setComments] = useState<ReviewComment[]>([]);
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
        <CommentInputForm afterPosting={reloadComments}/>
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
    const [duringEditing, setDuringEditing] = useState<boolean>(false)
    const modificationContent = (isAdmin || isCommentAuthor)
        ? <EditBar handleDelete={createCommentDeleteHandler(comment.id, afterDeleting, setErrorMessage)}
                   deleteConfirmationQuery={"Czy na pewno chcesz usunąć komentarz?"}
                   handleEdit={(_) => setDuringEditing(true)}
                   canEdit={isCommentAuthor}/> : null; //todo, different issue though

    return <>
        <div>{comment.authorUsername} {modificationContent}</div>
        <div>{comment.text}</div>
        <ErrorBox message={errorMessage}/>
    </>
}

interface CommentInputFormProps {
    afterPosting: Function
}

function CommentInputForm({afterPosting}: CommentInputFormProps) {
    const [comment, setComment] = useState<string>("");
    const {courseId, authorUsername} = useParams(); // todo: should they be set here or above and passed down?
    function handleCommentSubmit() {
        if (comment === "") return; //todo: inform user comment can't be blank
        const request: CommentRequest = {
            text: comment
        }

        CommentService.postComment(request, courseId!, authorUsername!)
            .then(() => {
                afterPosting();
                setComment("")
            })
            .catch(e => console.log(e));
    }

    return <div>
    <textarea
        placeholder="Twój komentarz"
        onChange={e => setComment(e.target.value)}
        value={comment}
    />
        <button onClick={handleCommentSubmit}>Dodaj komentarz</button>
    </div>
}

interface CommentEditFormProps {
    afterPosting: Function
    commentId: string
    setEditingForParent: Function
}

function CommentEcitForm({afterPosting, commentId, setEditingForParent}: CommentEditFormProps) {
    const [comment, setComment] = useState<string>("");
    const {courseId, authorUsername} = useParams(); // todo: should they be set here or above and passed down?
    function handleCommentSubmit() {
        if (comment === "") return; //todo: inform user comment can't be blank
        const request: CommentRequest = {
            text: comment
        }
        //todo: different post probably
        CommentService.postComment(request, courseId!, authorUsername!)
            .then(() => {
                afterPosting();
                setEditingForParent(false);
            })
            .catch(e => console.log(e));
    }

    return <div>
    <textarea
        placeholder="Twój komentarz"
        onChange={e => setComment(e.target.value)}
        value={comment}
    />
        <button onClick={handleCommentSubmit}>Edytuj komentarz</button>
        <button onClick={setEditingForParent(false)}>Anuluj</button>
    </div>
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