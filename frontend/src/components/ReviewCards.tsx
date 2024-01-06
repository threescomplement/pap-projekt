import {Review, ReviewService} from "../lib/Review";
import {Link, useParams} from "react-router-dom";
import {EditBar} from "./EditBar";
import React, {useState} from "react";
import useUser from "../hooks/useUser";
import {User} from "../lib/User";
import ErrorBox from "./ErrorBox";
import {ratingToPercentage} from "../lib/utils";

interface ReviewCardProps {
    review: Review;
    afterDeleting: Function
}

export function ReviewCardWithLink(props: ReviewCardProps) {
    return <div>
        <ReviewCardWithoutLink {...props}/>
        <Link to={`reviews/${props.review.authorUsername}`}> Czytaj więcej </Link>
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
            handleDelete={(e) => handleDeleteReview(e)}
            deleteConfirmationQuery={"Czy na pewno chcesz usunąć opinię?"}/> : null;

    function handleDeleteReview(e: React.MouseEvent) {
        e.preventDefault()
        ReviewService.deleteReview(courseId!, review.authorUsername)
            .then(deleted => {
                (deleted) ? afterDeleting() : setErrorMessage('Przy usuwaniu opinii wystąpił błąd. ' +
                    'Spróbuj ponownie lub skontaktuj się z administracją...');
            })
    }

    return <>
        <div>{review.authorUsername} {modificationContent}</div>
        <div>
            <p>Jak łatwy: {ratingToPercentage(review.easeRating)}</p>
            <p>Jak interesujący: {ratingToPercentage(review.interestRating)}</p>
            <p>Jak angażujący: {ratingToPercentage(review.engagementRating)}</p>
        </div>
        <div>{review.opinion}</div>
        <ErrorBox message={errorMessage}/>
    </>
}

