import CourseList from "../../components/CourseList";
import {useEffect, useState} from "react";
import {Course, fetchCourses} from "../../lib/Course";
import useUser from "../../hooks/useUser";

export function Courses() {
    const {user} = useUser();
    const [courses, setCourses] = useState<Course[]>([]);
    const [isLoaded, setIsLoaded] = useState(false);
    const [query, setQuery] = useState("");

    useEffect(() => {
        fetchCourses(user!)
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
        <h1>Courses</h1>
        <input type="text" onChange={e => setQuery(e.target.value)}/>
        {content}
    </>
}