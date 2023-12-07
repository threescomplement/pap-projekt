import {Review} from "../lib/Review";
import {Link} from "react-router-dom";
import React from "react";

interface ReviewCardProps {
    review: Review;
}

export function ReviewCardWithLink({review}: ReviewCardProps) {
    return <>
        <div>{review.authorUsername}</div>
        <div>{"Ocena: " + review.overallRating}</div>
        <div>{review.opinion}</div>
        <div>
            {<Link to={"comments/" + review.authorUsername}> Czytaj więcej </Link>}
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

