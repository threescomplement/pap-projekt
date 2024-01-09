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
        navigate(`/courses/${courseId}/reviewDeleted`)
    }

    function afterDeletingComment() {
        reloadComments();
        setMessage("Komentarz został usunięty.")
    }

    return <div>
        <ReviewCardWithoutLink review={review} afterDeleting={afterDeletingReview}/>
        <MessageBox message={message}/>
        <CommentList comments={comments} afterDeleting={afterDeletingComment} afterEditing={reloadComments}/>
        <CommentInputForm afterPosting={reloadComments} courseId={courseId!}
                          reviewAuthorUsername={review.authorUsername}/>
    </div>
}

interface CommentListProps {
    comments: ReviewComment[],
    afterDeleting: Function
    afterEditing: Function
}

export function CommentList({comments, afterDeleting, afterEditing}: CommentListProps) {
    return <ul>
        {comments //todo: sort
            .map(c => <li
                key={c.id}><CommentCard comment={c} afterDeleting={afterDeleting} afterEditing={afterEditing}/>
            </li>)}
    </ul>
}

interface CommentCardProps {
    comment: ReviewComment;
    afterDeleting: Function;
    afterEditing: Function;
}

function CommentCard({comment, afterDeleting, afterEditing}: CommentCardProps) {
    const user: User = useUser().user!
    const isAdmin = user.roles[0] === "ROLE_ADMIN";
    const isCommentAuthor = user.username === comment.authorUsername;
    const [errorMessage, setErrorMessage] = useState<string>("");
    const [duringEditing, setDuringEditing] = useState<boolean>(false)
    const modificationContent = (isAdmin || isCommentAuthor)
        ? <EditBar handleDelete={(e) => handleDeleteComment(e)}
                   deleteConfirmationQuery={"Czy na pewno chcesz usunąć komentarz?"}
                   handleEdit={() => {
                       setDuringEditing(true);
                   }}
                   canEdit={isCommentAuthor}/> : null;

    function handleDeleteComment(e: React.MouseEvent) {
        e.preventDefault()
        CommentService.deleteComment(comment.id)
            .then(deleted => {
                deleted ? afterDeleting() : setErrorMessage("'Przy usuwaniu opinii wystąpił błąd. " +
                    "Spróbuj ponownie lub skontaktuj się z administracją...");
            })
    }

    return duringEditing
        ? <CommentEditForm afterPosting={afterEditing} commentId={comment.id} setEditingForParent={setDuringEditing}
                           oldContent={comment.text}/>
        : <div>
            <div>{comment.authorUsername} {modificationContent}</div>
            <div>{comment.text}</div>
            <ErrorBox message={errorMessage}/>
        </div>

}

interface CommentInputFormProps {
    afterPosting: Function
    courseId: string
    reviewAuthorUsername: string
}

function CommentInputForm({afterPosting, courseId, reviewAuthorUsername}: CommentInputFormProps) {
    const [comment, setComment] = useState<string>("");

    function handleCommentSubmit() {
        if (comment === "") return; //todo: inform user comment can't be blank
        const request: CommentRequest = {
            text: comment
        }

        CommentService.postComment(request, courseId, reviewAuthorUsername)
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
    oldContent: string
}

function CommentEditForm({afterPosting, commentId, setEditingForParent, oldContent}: CommentEditFormProps) {
    const [comment, setComment] = useState<string>(oldContent);

    function handleCommentSubmit() {
        if (comment === "") return; //todo: inform user comment can't be blank
        const request: CommentRequest = {
            text: comment
        }
        CommentService.editComment(commentId, request)
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
        <button onClick={() => setEditingForParent(false)}>Anuluj</button>
    </div>
}
