import {Review, ReviewService} from "../lib/Review";
import {Link, useParams} from "react-router-dom";
import {EditBar} from "./EditBar";
import React, {useState} from "react";
import useUser from "../hooks/useUser";
import {User} from "../lib/User";
import ErrorBox from "./ErrorBox";

interface ReviewCardProps {
    review: Review;
    afterDeleting: Function
}

export function ReviewCardWithLink(props: ReviewCardProps) {
    return <div>
        <ReviewCardWithoutLink {...props}/>
        <Link to={"reviews/" + props.review.authorUsername}> Czytaj więcej </Link>
    </div>
}

export function ReviewCardWithoutLink({review, afterDeleting}: ReviewCardProps) {
    const {courseId} = useParams()
    const [errorMessage, setErrorMessage] = useState<string>("")
    const user: User = useUser().user!;
    const isAdmin: boolean = user.roles[0] === "ROLE_ADMIN";
    const isReviewAuthor: boolean = review.authorUsername === user.username;
    const modificationContent = (isReviewAuthor || isAdmin) ?
        <EditBar
            handleDelete={createDeleteHandler(courseId!, review.authorUsername, afterDeleting, setErrorMessage)}/> : null;


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
                (deleted) ? afterDeleting() : errorBoxSetter('Przy usuwaniu opinii wystąpił błąd. ' +
                    'Spróbuj ponownie lub skontaktuj się z administracją...');
            })
    }
}