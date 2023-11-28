import {fetchCourse, Course} from "../../lib/Course";
import {useParams} from "react-router-dom";
import React, {useEffect, useState} from "react";
import CourseDetails from "../../components/CourseDetails";
import useUser from "../../hooks/useUser";


export default function SingleCourse() {
    const {courseId} = useParams();
    const {user} = useUser();
    const [course, setCourse] = useState<Course | null>(null);
    const [isLoaded, setIsLoaded] = useState(false);

    useEffect(() => {
        if (courseId == undefined) {
            console.error("courseId is null");
            return;
        }
        fetchCourse(courseId, user!)
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
        <CourseDetails {...course}/>
    </>
}