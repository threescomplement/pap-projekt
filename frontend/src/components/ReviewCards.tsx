import {Review, ReviewService} from "../lib/Review";
import {Link, useParams} from "react-router-dom";
import {EditBar} from "./EditBar";
import React from "react";
import useUser from "../hooks/useUser";
import {User} from "../lib/User";

interface ReviewCardProps {
    review: Review;
    refreshParent: Function
}

export function ReviewCardWithLink({review, refreshParent}: ReviewCardProps) {
    return <div>
        {/* todo: if deleted here, route one page back*/}
        <ReviewCardWithoutLink review={review} refreshParent={refreshParent}/>
        {<Link to={"reviews/" + review.authorUsername}> Czytaj więcej </Link>}
    </div>
}

export function ReviewCardWithoutLink({review, refreshParent}: ReviewCardProps) {
    const {courseId} = useParams()
    const user: User = useUser().user!;
    const isAdmin: boolean = user.roles[0] === "ROLE_ADMIN";
    const ownComment: boolean = review.authorUsername === user.username;
    const modificationContent = (ownComment || isAdmin) ?
        <EditBar handleDelete={createDeleteHandler(courseId!, review.authorUsername, refreshParent)}/> : null;

    return <>
        <div>{review.authorUsername} {modificationContent}</div>
        <div>{"Ocena: " + review.overallRating}</div>
        <div>{review.opinion}</div>
    </>
}


function createDeleteHandler(courseId: string, username: string, afterDeleting: Function): React.MouseEventHandler {
    return async event => {
        event.preventDefault()
        if (window.confirm("Czy na pewno chcesz usunąć swoją opinię?")) {
            ReviewService.deleteReview(courseId, username)
                .then(deleted => {
                    let feedback = deleted ? 'Review deleted successfully!' : 'Failed to delete review! Please try again...';
                    alert(feedback);
                    afterDeleting();
                })
        }
    }
}