import {Review, ReviewService} from "../lib/Review";
import {Link, useNavigate, useParams} from "react-router-dom";
import {EditBar} from "./EditBar";
import React, {useEffect, useState} from "react";
import useUser from "../hooks/useUser";
import {User} from "../lib/User";
import ErrorBox from "./ErrorBox";
import {ratingToPercentage} from "../lib/utils";
import styles from "../ui/components/ReviewAndCommentCards.module.css";
import ReportBox from "./ReportBox";
import RatingProgressBar from "./RatingProgressBar";
import {Course} from "../lib/Course";
import api from "../lib/api";

interface ReviewCardProps {
    review: Review;
    afterDeleting: Function
    renderCourseLink: boolean
}

export function ReviewCardWithLink(props: ReviewCardProps) {
    const [courseId, setCourseId] = useState<Course | null>(null);
    useEffect(() => {
        api.get(props.review._links.course.href)
            .then(response => response.json())
            .then(course => course.id)
            .then(courseId => setCourseId(courseId))
    }, []);

    return <div className={styles.cardContainer}>
        <ReviewCardWithoutLink {...props}/>
        {courseId != null ?
            <Link to={`/courses/${courseId}/reviews/${props.review.authorUsername}`}
                  className={styles.readMoreLink}> Czytaj
                więcej </Link> : <p>Loading review link...</p>}
    </div>
}

export function ReviewCardWithoutLink({review, afterDeleting, renderCourseLink}: ReviewCardProps) {
    const {courseId} = useParams()
    const [errorMessage, setErrorMessage] = useState<string>("")
    const user: User = useUser().user!;
    const [course, setCourse] = useState<Course | null>(null)
    const navigate = useNavigate();
    const isAdmin: boolean = user.roles[0] === "ROLE_ADMIN";
    const isReviewAuthor: boolean = review.authorUsername === user.username;
    const modificationContent = (isReviewAuthor || isAdmin) ?
        <EditBar
            handleDelete={(e) => handleDeleteReview(e)}
            deleteConfirmationQuery={"Czy na pewno chcesz usunąć opinię?"}
            handleEdit={(_) => navigate(`/courses/${courseId}/writeReview`)}
            canEdit={isReviewAuthor}
        /> : null;

    function handleDeleteReview(e: React.MouseEvent) {
        e.preventDefault()
        ReviewService.deleteReview(courseId!, review.authorUsername)
            .then(deleted => {
                (deleted) ? afterDeleting() : setErrorMessage('Przy usuwaniu opinii wystąpił błąd. ' +
                    'Spróbuj ponownie lub skontaktuj się z administracją...');
            })
    }

    useEffect(() => {
        api.get(review._links.course.href)
            .then(response => response.json())
            .then(course => setCourse(course))
    }, []);


    return <div className={styles.cardContainer}>
        <div className={styles.cardHeader}>
            <div className={styles.usernameAndProgressContainer}>
                <p className={styles.cardAuthor}>{review.authorUsername}
                    {course != null && renderCourseLink && <> o kursie<Link className={styles.courseLink} to={`/courses/${course.id}`}>{course.name}</Link></>}
                        </p>
            </div>
            <div className={styles.cardButtonContainer}>{modificationContent}
                <ReportBox reportedEntity={review}/></div>
        </div>
        <div className={styles.ratingsContainer}>
            <div>
                <p className={styles.rating}>Jak łatwy: {ratingToPercentage(review.easeRating)}</p>
                <RatingProgressBar value={review.easeRating}/>
            </div>
            <div>
                <p className={styles.rating}>Jak interesujący: {ratingToPercentage(review.interestRating)}</p>
                <RatingProgressBar value={review.interestRating}/>
            </div>
            <div>
                <p className={styles.rating}>Jak angażujący: {ratingToPercentage(review.engagementRating)}</p>
                <RatingProgressBar value={review.engagementRating}/>
            </div>
        </div>
        <div className={styles.opinion}>{review.opinion}</div>
        <ErrorBox message={errorMessage}/>
    </div>
}

