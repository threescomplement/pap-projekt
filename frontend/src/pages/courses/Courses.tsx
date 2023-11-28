import CourseList from "../../components/CourseList";
import {useEffect, useState} from "react";
import {Course, fetchCoursesByName} from "../../lib/Course";

export function Courses() {
    const [courses, setCourses] = useState<Course[]>([]);
    const [isLoaded, setIsLoaded] = useState(false);
    const [query, setQuery] = useState("");

    useEffect(() => {
        fetchCoursesByName(query)
            .then(cs => {
                setCourses(cs);
                setIsLoaded(true);
            })
            .catch(e => console.error(e));
    }, [query]);

    const content = isLoaded
        ? <CourseList courses={courses}/>
        : <p>Loading...</p>

    return <>
        <h1>Kursy</h1>
        <input type="text" onChange={e => setQuery(e.target.value)}/>
        {content}
    </>
}