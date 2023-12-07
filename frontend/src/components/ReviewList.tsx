import {Review} from "../lib/Review";
import React from "react";
import {ReviewCardWithLink} from "./ReviewCards";

interface ReviewListProps {
    reviews: Review[]
}

export function ReviewList({reviews}: ReviewListProps) {
    return <ul>
        {reviews
            //todo .sort by timestamps
            .map((r) => (
                <li key={r.id}>
                    <ReviewCardWithLink data={r}/>
                </li>
            ))}
    </ul>
}

//todo: edit, delete, etc based on the user logged in