import {Review} from "../lib/Review";
import {ReviewCardWithLink} from "./ReviewCards";
import React from "react";

interface ReviewListProps {
    reviews: Review[]
    refreshParent: Function
    renderCourseLinks: boolean
}

export default function ReviewList({reviews, refreshParent, renderCourseLinks}: ReviewListProps) {
    const sortedReviews = reviews.sort((a, b) => {
        const dateA = new Date(a.created);
        const dateB = new Date(b.created);
        return dateA.getTime() - dateB.getTime();
    });
    return <ul>
        {sortedReviews
            //todo: factor out sorting for reviews and comments into util
            .map((r) => (
                <li key={r.authorUsername}>
                    <ReviewCardWithLink review={r} afterDeleting={refreshParent} renderCourseLink={renderCourseLinks}/>
                </li>
            ))}
    </ul>
}

