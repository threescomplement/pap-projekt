import {Review, ReviewService} from "../lib/Review";
import {Link, useParams} from "react-router-dom";
import {EditBar} from "./EditBar";
import React, {useState} from "react";
import useUser from "../hooks/useUser";
import {User} from "../lib/User";
import ErrorBox from "./ErrorBox";
import {ConfirmationPopup} from "./ConfirmationPopup";

interface ReviewCardProps {
    review: Review;
    refreshParent: Function
}

export function ReviewCardWithLink({review, refreshParent}: ReviewCardProps) {
    return <div>
        <ReviewCardWithoutLink review={review} refreshParent={refreshParent}/>
        {<Link to={"reviews/" + review.authorUsername}> Czytaj więcej </Link>}
    </div>
}

export function ReviewCardWithoutLink({review, refreshParent}: ReviewCardProps) {
    const {courseId} = useParams()
    const [errorMessage, setErrorMessage] = useState<string>("")
    const user: User = useUser().user!;
    const isAdmin: boolean = user.roles[0] === "ROLE_ADMIN";
    const isReviewAuthor: boolean = review.authorUsername === user.username;
    const modificationContent = (isReviewAuthor || isAdmin) ?
        <EditBar
            handleDelete={createDeleteHandler(courseId!, user.username, refreshParent, setErrorMessage)}/> : null;

    async function handleCommentRemoval() {
        ReviewService.deleteReview(courseId!, user.username)
            .then(deleted => {
                if (!deleted) {
                    setErrorMessage('Przy usuwaniu opinii wystąpił błąd. ' + 'Spróbuj ponownie lub skontaktuj się z administracją...');
                }
                refreshParent();
            });
    }

    return <>
        <div>{review.authorUsername} {modificationContent}</div>
        <div>{"Ocena: " + review.overallRating}</div>
        <div>{review.opinion}</div>
        <ErrorBox message={errorMessage}/>
    </>
}


function createDeleteHandler(courseId: string, username: string, afterDeleting: Function, errorBoxSetter: Function): React.MouseEventHandler {
    return async event => {
        event.preventDefault()
        ReviewService.deleteReview(courseId, username)
            .then(deleted => {
                if (!deleted) errorBoxSetter('Przy usuwaniu opinii wystąpił błąd. ' +
                    'Spróbuj ponownie lub skontaktuj się z administracją...');
                afterDeleting();
            })
    }
}