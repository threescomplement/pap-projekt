import {useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import {Review, ReviewService} from "../../lib/Review";
import {ReviewDetails} from "../../components/ReviewDetails";

export function SingleReview() {
    const {courseId, authorUsername} = useParams();
    const [review, setReview] = useState<Review | null>(null)

    useEffect(() => {
        ReviewService.fetchReviewByCourseIdAndAuthor(courseId!, authorUsername!)
            .then(r => {
                console.log(r);
                setReview(r);
            })
    }, [authorUsername, courseId]);

    return review != null ? <ReviewDetails review={review} /> : <h1>Loading..</h1>
}