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
        {<Link to={`reviews/${props.review.authorUsername}`}> Czytaj więcej </Link>}
    </div>
}

export function ReviewCardWithoutLink({review, afterDeleting}: ReviewCardProps) {
    const {courseId} = useParams()
    const [errorMessage, setErrorMessage] = useState<string>("")
    const user: User = useUser().user!;
    const isAdmin: boolean = user.roles[0] === "ROLE_ADMIN";
    const isReviewAuthor: boolean = review.authorUsername === user.username;
    const modificationContent = (isReviewAuthor || isAdmin) ?
        <EditBar handleDelete={createReviewDeleteHandler(courseId!, review.authorUsername, afterDeleting, setErrorMessage)}
                  deleteConfirmationQuery={"Czy na pewno chcesz usunąć opinię?"}/> : null;


    return <>
        <div>{review.authorUsername} {modificationContent}</div>
        <p>{`Ocena: ${review.overallRating}`}</p>
        <p>{review.opinion}</p>
        <ErrorBox message={errorMessage}/>
    </>
}


function createReviewDeleteHandler(courseId: string, username: string, afterDeleting: Function, errorBoxSetter: Function): React.MouseEventHandler {
    return async event => {
        event.preventDefault()
        ReviewService.deleteReview(courseId, username)
            .then(deleted => {
                (deleted) ? afterDeleting() : errorBoxSetter('Przy usuwaniu opinii wystąpił błąd. ' +
                    'Spróbuj ponownie lub skontaktuj się z administracją...');
            })
    }
}