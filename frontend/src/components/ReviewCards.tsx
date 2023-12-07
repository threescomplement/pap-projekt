import {Review} from "../lib/Review";
import {Link} from "react-router-dom";
import React from "react";

interface ReviewCardProps {
    data: Review;
}

export function ReviewCardWithLink({data}: ReviewCardProps) {
    return <>
        <div>{data.authorUsername}</div>
        <div>{"Ocena: " + data.overallRating}</div>
        <div>{data.opinion}</div>
        <div>
            {<Link to={"comments/" + data.authorUsername}> Czytaj więcej </Link>}
        </div>
    </>
}

export function ReviewCardWithoutLink({data}: ReviewCardProps) {
    return <>
        <div>{data.authorUsername}</div>
        <div>{"Ocena: " + data.overallRating}</div>
        <div>{data.opinion}</div>
    </>
}

