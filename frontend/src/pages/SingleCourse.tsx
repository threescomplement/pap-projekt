import {attemptCourseDataRequest, ICourse} from "../lib/Course";
import {useParams} from "react-router-dom";
import React, {useEffect, useState} from "react";
import CourseData from "../components/CourseData";

interface SingleCourseProps {
    course: ICourse
}

export default function SingleCourse() {
    const {courseId} = useParams();
    const [course, setCourse] = useState<ICourse | null>(null);
    const [isLoaded, setIsLoaded] = useState(false);

    useEffect(() => {
        if (courseId == undefined) {
            console.error("courseId is null");
            return;
        }
        attemptCourseDataRequest(courseId)
            .then(c => {
                    setCourse(c);
                    setIsLoaded(true);
                }
            )
    }, [courseId]);

    if (course == null || courseId == null || !isLoaded) {
        return <>
            <h1>{courseId}</h1>
            <p>Loading...</p>
        </>
    }

    return <>
        <CourseData {...course}/>
    </>
}