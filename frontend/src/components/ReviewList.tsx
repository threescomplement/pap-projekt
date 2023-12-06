import {Review} from "../lib/Review";
import {CommentReviewCard} from "./CommentReview";

interface ReviewListProps {
    reviews: Review[]
}
function ReviewList({reviews}: ReviewListProps) {
    return <ul>
        {reviews.map((r) => (
            <li key={r.id}>
                <CommentReviewCard data={r}/>
            </li>
        ))}
    </ul>
}
