import {Review, ReviewService} from "../lib/Review";
import {Link, useParams} from "react-router-dom";
import {EditBar} from "./EditBar";
import React from "react";
import useUser from "../hooks/useUser";
import {User} from "../lib/User";

interface ReviewCardProps {
    review: Review;
}

export function ReviewCardWithLink({review}: ReviewCardProps) {
    const {courseId} = useParams()
    const user: User = useUser().user!;
    const isAdmin: boolean = user.roles[0] === "ROLE_ADMIN";
    const ownComment: boolean = review.authorUsername === user.username;
    const modificationContent = (ownComment || isAdmin) ?
        <EditBar handleDelete={createDeleteHandler(courseId!, review.authorUsername)}/> : null; // todo

    return <>
        <div>{review.authorUsername} {modificationContent}</div>
        <div>{"Ocena: " + review.overallRating}</div>
        <div>{review.opinion}</div>
        <div>
            {<Link to={"reviews/" + review.authorUsername}> Czytaj więcej </Link>}
        </div>
    </>
}

export function ReviewCardWithoutLink({review}: ReviewCardProps) {
    return <>
        <div>{review.authorUsername}</div>
        <div>{"Ocena: " + review.overallRating}</div>
        <div>{review.opinion}</div>
    </>
}


function createDeleteHandler(courseId: string, username: string): React.MouseEventHandler {
    return async event => {
        event.preventDefault();
        let deleted = await ReviewService.deleteReview(courseId, username);
        const feedback = deleted ? 'Review deleted successfully!' : 'Failed to delete review! Please try again...';
        alert(feedback);
    };
}