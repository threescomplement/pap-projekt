import React, {useCallback, useEffect, useState} from "react";
import {Link} from "react-router-dom";
import {Course} from "../lib/Course";
import {Teacher, TeacherService} from "../lib/Teacher";
import {Review, ReviewService} from "../lib/Review";
import {ReviewCardWithLink} from "./ReviewCards";
import MessageBox from "./MessageBox";
import {ratingToPercentage} from "../lib/utils";
import styles from "../pages/courses/SingleCourse.module.css"

export default function CourseDetails(course: Course) {
    const [teacher, setTeacher] = useState<Teacher | null>(null);
    const [teacherLoaded, setTeacherLoaded] = useState(false);
    const [reviews, setReviews] = useState<Review[]>([]);
    const [message, setMessage] = useState<string>("");
    const memorizedReloadReviews = useCallback(reloadReviews, [course]);

    function reloadReviews() {
        ReviewService.fetchReviewsByCourse(course)
            .then((r) => {
                setReviews(r);
            })
            .catch((e) => console.log(e));
    }

    function afterReviewDelete() {
        reloadReviews();
        setMessage("Opinia została usunięta.");
    }

    useEffect(() => {
        TeacherService.fetchTeacherByCourse(course)
            .then((t) => {
                setTeacher(t);
                setTeacherLoaded(true);
            })
            .catch((e) => console.log(e));

        memorizedReloadReviews();
    }, [course, memorizedReloadReviews]);

    const teacherContent = teacher != null && teacherLoaded ? (
        <Link to={`/teachers/${course.teacherId}`}> {teacher.name} </Link>
    ) : (
        <span>COURSE_TEACHER_PLACEHOLDER</span>
    );

    const moduleContent = course.module != null ? (
        <p>Moduł: {course.module}</p>
    ) : (
        <p>Ten kurs nie jest przypisany do żadnego modułu</p>
    );

    const reviewContent =
        reviews.length === 0 ? (
            <div>Ten kurs nie ma jeszcze opinii</div>
        ) : (
            <div>{<ReviewList reviews={reviews} refreshParent={afterReviewDelete}/>}</div>
        );

    return (
        <div>
            <h1>{course.name}</h1>
            <p>Lektor: {teacherContent}</p>
            <div className={styles.courseInfoContainer}>
                <div className={styles.courseInfo}>
                    <h2>Informacje o kursie:</h2>
                    {moduleContent}
                    <p>Poziom: {course.level}</p>
                    <p>Typ kursu: {course.type}</p>
                </div>
                <div className={styles.courseInfo}>
                    <h2>Uśrednione opinie</h2>
                    <p>Jak łatwy? {ratingToPercentage(course.averageEaseRating)}</p>
                    <p>Jak interesujący? {ratingToPercentage(course.averageInterestRating)}</p>
                    <p>Jak angażujący? {ratingToPercentage(course.averageEngagementRating)}</p>
                </div>
            </div>
            <h2>Opinie</h2>
            <MessageBox message={message}/>
            {reviewContent}
        </div>
    );
}

interface ReviewListProps {
    reviews: Review[];
    refreshParent: Function;
}

function ReviewList({reviews, refreshParent}: ReviewListProps) {
    return (
        <ul>
            {reviews.map((r) => (
                <li key={r.authorUsername}>
                    <ReviewCardWithLink review={r} afterDeleting={refreshParent}/>
                </li>
            ))}
        </ul>
    );
}
