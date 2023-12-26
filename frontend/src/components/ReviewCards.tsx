import {Review, ReviewService} from "../lib/Review";
import {Link, useNavigate, useParams} from "react-router-dom";
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
        <ReviewCardWithoutLink review={review} refreshParent={refreshParent}/>
        {<Link to={"reviews/" + review.authorUsername}> Czytaj więcej </Link>}
    </div>
}

export function ReviewCardWithoutLink({review, refreshParent}: ReviewCardProps) {
    const {courseId} = useParams()
    const user: User = useUser().user!;
    const navigate = useNavigate();
    const isAdmin: boolean = user.roles[0] === "ROLE_ADMIN";
    /* todo: should admins be able to edit comments or just delete them? if they can edit, the way things are edited
        have to be changed quite a bit since posting as useUser() will not work here...*/
    const isReviewAuthor: boolean = review.authorUsername === user.username;
    const modificationContent = (isReviewAuthor || isAdmin) ?
        <EditBar
            handleDelete={createDeleteHandler(courseId!, review.authorUsername, refreshParent)}
            handleEdit={(e)=> navigate("/courses/"+courseId+"/writeReview")}
            canEdit={isReviewAuthor}
        /> : null;

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