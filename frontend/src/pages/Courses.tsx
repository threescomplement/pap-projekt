import CourseList from "../components/CourseList";
import {useEffect, useState} from "react";
import {Course} from "../lib/Course";

export function Courses() {
    const [courses, setCourses] = useState<Course[]>([]);
    const [isLoaded, setIsLoaded] = useState(false);

    useEffect(() => {
        fetch(`${process.env.REACT_APP_API_ROOT}courses`)
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