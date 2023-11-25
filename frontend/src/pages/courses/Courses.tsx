import CourseList from "../../components/CourseList";
import {useEffect, useState} from "react";
import {Course} from "../../lib/Course";

export function Courses() {
    const [courses, setCourses] = useState<Course[]>([]);
    const [isLoaded, setIsLoaded] = useState(false);
    const [query, setQuery] = useState("");

    useEffect(() => {
        fetch(`${process.env.REACT_APP_API_ROOT}courses?name=${query}`) // TODO implement in backend
            .then(response => response.json())
            .then(json => {
                setCourses(json._embedded.courses);
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