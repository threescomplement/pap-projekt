import {Review} from "../lib/Review";
import {ReviewCardWithLink} from "./ReviewCards";
import React from "react";

interface ReviewListProps {
    reviews: Review[]
    refreshParent: Function
}

export default function ReviewList({reviews, refreshParent}: ReviewListProps) {
    return <ul>
        {reviews
            //todo .sort by timestamps
            .map((r) => (
                <li key={r.authorUsername}>
                    <ReviewCardWithLink review={r} afterDeleting={refreshParent}/>
                </li>
            ))}
    </ul>
}

