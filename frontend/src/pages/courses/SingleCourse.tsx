import {Course, CourseService} from "../../lib/Course";
import {Link, useParams} from "react-router-dom";
import React, {useEffect, useState} from "react";
import CourseDetails from "../../components/CourseDetails";
import {ReviewService} from "../../lib/Review";
import useUser from "../../hooks/useUser";
import styles from "./SingleCourse.module.css"

export default function SingleCourse() {
    const username = useUser().user!.username
    const {courseId} = useParams();
    const [course, setCourse] = useState<Course | null>(null);
    const [isLoaded, setIsLoaded] = useState<boolean>(false);
    const [userHasReview, setUserHasReview] = useState<boolean>(false);

    useEffect(() => {
        if (courseId == null) {
            console.error("courseId is null");
            return;
        }
        CourseService.fetchCourse(courseId)
            .then(c => {
                    setCourse(c);
                    setIsLoaded(true);
                }
            )

        ReviewService.fetchReviewByCourseIdAndAuthor(courseId, username)
            .then(r => r == null ? setUserHasReview(false) : setUserHasReview(true));
    }, [courseId, username]);

    if (course == null || courseId == null || !isLoaded) {
        return <div>
            <h1>{courseId}</h1>
            <p>Loading...</p>
        </div>
    }

    return <div className={styles.singleCourseContainer}>
        <CourseDetails {...course}/>
        <Link className={styles.writeReviewLink} to="writeReview">{userHasReview ? "Edytuj swoją opinię" : "Napisz opinię"}</Link>
    </div>
}