import {Course} from "../lib/Course";
import {Teacher, TeacherService} from "../lib/Teacher";
import React, {useCallback, useEffect, useState} from "react";
import {Link} from "react-router-dom";
import {Review, ReviewService} from "../lib/Review";
import {ReviewCardWithLink} from "./ReviewCards";
import MessageBox from "./MessageBox";
import {ratingToPercentage} from "../lib/utils";

export default function CourseDetails(course: Course) {
    const [teacher, setTeacher] = useState<Teacher | null>(null);
    const [teacherLoaded, setTeacherLoaded] = useState(false);
    const [reviews, setReviews] = useState<Review[]>([]);
    const [message, setMessage] = useState<string>("");
    const memorizedReloadReviews = useCallback(reloadReviews, [course])

    function reloadReviews() {
        ReviewService.fetchReviewsByCourse(course)
            .then(r => {
                setReviews(r);
            })
            .catch(e => console.log(e));
    }

    function afterReviewDelete() {
        reloadReviews();
        setMessage("Opinia została usunięta.");
    }

    useEffect(() => {
        TeacherService.fetchTeacherByCourse(course)
            .then(t => {
                setTeacher(t);
                setTeacherLoaded(true);
            })
            .catch(e => console.log(e));

        memorizedReloadReviews();

    }, [course, memorizedReloadReviews]);

    const teacherContent = (teacher != null && teacherLoaded)
        ? <Link className="TeacherLink" to={`/teachers/${course.teacherId}`}> {teacher.name} </Link>
        : <span className="TeacherLink">COURSE_TEACHER_PLACEHOLDER</span>;

    const moduleContent = course.module != null
        ? <p className="CourseInfo">Moduł: {course.module}</p>
        : <p className="CourseInfo">Ten kurs nie jest przypisany do żadnego modułu</p>


    const reviewContent = reviews.length === 0
        ? <div>Ten kurs nie ma jeszcze opinii</div>
        : <div>{<ReviewList reviews={reviews} refreshParent={afterReviewDelete}/>}</div>

    return <>
        <h1>{course.name}</h1>
        <p className="TeacherHeader">Lektor: {teacherContent}</p>
        <h2>Informacje o kursie:</h2>
        {moduleContent}
        <p className="CourseInfo">Poziom: {course.level}</p>
        <p className="CourseInfo">Typ kursu: {course.type}</p>
        <h2>Uśrednione opinie</h2>
        <div>
            <p>Jak łatwy? {ratingToPercentage(course.averageEaseRating)}</p>
            <p>Jak interesujący? {ratingToPercentage(course.averageInterestRating)}</p>
            <p>Jak angażujący? {ratingToPercentage(course.averageEngagementRating)}</p>
        </div>
        <h2 className="OpinionsSection">Opinie</h2>
        <MessageBox message={message}/>
        {reviewContent}
    </>
}


interface ReviewListProps {
    reviews: Review[]
    refreshParent: Function
}

function ReviewList({reviews, refreshParent}: ReviewListProps) {
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

