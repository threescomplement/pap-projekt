import {Course, CourseService} from "../../lib/Course";
import {Link, useParams} from "react-router-dom";
import React, {useEffect, useState} from "react";
import CourseDetails from "../../components/CourseDetails";

export default function SingleCourse() {
    const {courseId} = useParams();
    const [course, setCourse] = useState<Course | null>(null);
    const [isLoaded, setIsLoaded] = useState(false);

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
    }, [courseId]);

    if (course == null || courseId == null || !isLoaded) {
        return <div>
            <h1>{courseId}</h1>
            <p>Loading...</p>
        </div>
    }

    return <div>
        <CourseDetails {...course}/>
        <Link to="writeReview">Napisz opinię</Link> {/* todo: change this to edytuj opinię if it has already been written*/}
    </div>
}