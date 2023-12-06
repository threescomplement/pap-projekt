import {Review} from "../lib/Review";
import {CommentReviewCard} from "./CommentReviewCard";

interface ReviewListProps {
    reviews: Review[]
}

function ReviewList({reviews}: ReviewListProps) {
    return <ul>
        {reviews
            //todo .sort by timestamps
            .map((r) => (
                <li key={r.id}>
                    <CommentReviewCard data={r}/>
                </li>
            ))}
    </ul>
}
