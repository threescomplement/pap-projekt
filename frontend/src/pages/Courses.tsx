import CourseList from "../components/CourseList";
import {useEffect, useState} from "react";
import {ICourse} from "../lib/Course";

export function Courses() {
    const [courses, setCourses] = useState<ICourse[]>([]);
    const [isLoaded, setIsLoaded] = useState(false);

    useEffect(() => {
        fetch("http://localhost:8080/api/courses")
            .then(response => response.json())
            .then(json => {
                setCourses(json._embedded.courses);
                setIsLoaded(true);
            })
            .catch(e => console.error(e));
    }, []);

    const content = isLoaded
        ? <CourseList courses={courses}/>
        : <p>Loading...</p>

    return <>
        <h1>Courses</h1>
        {content}
    </>
}