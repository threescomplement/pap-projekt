import {Teacher, TeacherService} from "../../lib/Teacher";
import {useParams} from "react-router-dom";
import React, {useEffect, useState} from "react";
import CourseList from "../../components/CourseList";
import {Course} from "../../lib/Course";
import styles from "../../ui/pages/SingleTeacher.module.css"
import AverageRatingDisplay from "../../components/AverageRatingDisplay";
import {Review, ReviewService} from "../../lib/Review";
import ReviewList from "../../components/ReviewList";
import MessageBox from "../../components/MessageBox";

interface SingleTeacherProps {
    teacher: Teacher
}

interface TeacherCourseListProps {
    teacherId: string
}

function TeacherData(props: SingleTeacherProps) {
    const teacher = props.teacher
    return <div>
        <h1>{teacher.name}</h1>
        <div className={styles.teacherInfoContainer}>
            <div className={styles.teacherInfo}>
                <AverageRatingDisplay entity={teacher}/>
            </div>
        </div>
    </div>
}

function TeacherCourseList({teacherId}: TeacherCourseListProps) {
    const [courses, setCourses] = useState<Course[]>([])

    useEffect(() => {
        TeacherService.fetchTeacherCourses(teacherId)
            .then(c => setCourses(c))
    }, [teacherId]);

    return <div className={styles.teacherCourseList}>
        <h2>Kursy</h2>
        <CourseList courses={courses}/>
    </div>
}


export default function SingleTeacher() {
    const {teacherId} = useParams();
    const [teacher, setTeacher] = useState<Teacher | null>(null);
    const [isLoaded, setIsLoaded] = useState(false);
    const [reviews, setReviews] = useState<Review[]>([])
    const [message, setMessage] = useState<string>("");

    function reloadReviews() {
        TeacherService.fetchTeacherReviews(teacherId!)
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
        if (teacherId == null) {
            console.error("teacherId is null");
            return;
        }

        TeacherService.fetchTeacher(teacherId).then((teacher) => {
            setTeacher(teacher);
            reloadReviews()
            setIsLoaded(true);
        }).catch(e => console.log("Error fetching data", e))
    }, [teacherId]);

    if (teacher == null || teacherId == null || !isLoaded) {
        return <>
            <h1>{teacherId}</h1>
            <p>Loading...</p>
        </>
    }


    const reviewContent = reviews.length === 0
        ? <div className={styles.noReviewsDisclaimer}>Kursy tego lektora nie mają jeszcze opinii</div>
        : <div className={styles.reviewListContainer}>{<ReviewList reviews={reviews}
                                                                   refreshParent={afterReviewDelete}/>}</div>


    return <div className={styles.singleTeacherContainer}>
        <TeacherData teacher={teacher}/>
        <TeacherCourseList teacherId={teacherId}/>
        <div className={styles.reviewContainer}>
        <h2>Opinie</h2>
        <MessageBox message={message}/>
        {reviewContent}
        </div>
    </div>
}