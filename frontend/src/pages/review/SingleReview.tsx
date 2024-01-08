import {useNavigate, useParams} from "react-router-dom";
import React, {useCallback, useEffect, useState} from "react";
import {Review, ReviewService} from "../../lib/Review";
import {CommentRequest, CommentService, ReviewComment} from "../../lib/ReviewComment";
import {ReviewCardWithoutLink} from "../../components/ReviewCards";
import MessageBox from "../../components/MessageBox";
import {User} from "../../lib/User";
import useUser from "../../hooks/useUser";
import {EditBar} from "../../components/EditBar";
import ErrorBox from "../../components/ErrorBox";
import "./SingleReview.css"

export function SingleReview() {
    const {courseId, authorUsername} = useParams();
    const [review, setReview] = useState<Review | null>(null)

    useEffect(() => {
        ReviewService.fetchReviewByCourseIdAndAuthor(courseId!, authorUsername!)
            .then(r => {
                console.log(r);
                setReview(r);
            })
    }, [authorUsername, courseId]);

    return review != null ? <ReviewDetails review={review} /> : <h1>Loading..</h1>
}

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
        navigate(`/courses/${courseId}/reviewDeleted`)
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
        <EditBar handleDelete={(e)=>handleDeleteComment(e)}
                 deleteConfirmationQuery={"Czy na pewno chcesz usunąć komentarz?"}
                 handleEdit={(_) => true}
                 canEdit={isCommentAuthor}
        /> : null;

    function handleDeleteComment(e: React.MouseEvent){
        e.preventDefault()
        CommentService.deleteComment(comment.id)
            .then(deleted => {
                deleted ? afterDeleting() : setErrorMessage("'Przy usuwaniu opinii wystąpił błąd. " +
                    "Spróbuj ponownie lub skontaktuj się z administracją...");
            })
    }

    return <>
        <div>{comment.authorUsername} {modificationContent}</div>
        <p>{comment.text}</p>
        <ErrorBox message={errorMessage}/>
    </>
}

