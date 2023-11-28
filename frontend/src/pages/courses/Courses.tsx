import CourseList from "../../components/CourseList";
import {useEffect, useState} from "react";
import {Course, CourseService} from "../../lib/Course";

export function Courses() {
    const [courses, setCourses] = useState<Course[]>([]);
    const [isLoaded, setIsLoaded] = useState(false);
    const [query, setQuery] = useState("");
    const [language, setLanguage] = useState("");

    useEffect(() => {
        CourseService.fetchCoursesByName(query)
            .then(cs => {
                setCourses(cs);
                setIsLoaded(true);
            })
            .catch(e => console.error(e));
    }, [query]);

    const content = isLoaded
        ? <CourseList courses={courses}/>
        : <p>Loading...</p>

    // TODO: allow multiple selections for filters
    return <>
        <h1>Kursy</h1>
        <input type="text" onChange={e => setQuery(e.target.value)}/>
        <select onChange={(e) => setLanguage(e.target.value)}>
            <option value="" selected hidden>Język</option>
            <option value="all">Wszystkie</option>
            <option value="eng">Angielski</option>
            <option value="spa">Hiszpański</option>
            <option value="ita">Włoski</option>
            <option value="deu">Niemiecki</option>
            <option value="fra">Francuski</option>
            <option value="pol">Polski</option>
            <option value="rus">Rosyjski</option>
            <option value="jpn">Japoński</option>
            <option value="chi">Chiński</option>
            <option value="nld">Niderlandzki</option>
            <option value="kor">Koreański</option>
        </select>
        <select>
            <option value="" selected hidden>Poziom</option>
            <option value="all">Wszystkie</option>
            <option value="a1">A1</option>
            <option value="a2">A2</option>
            <option value="b1">B1</option>
            <option value="b2">B2</option>
            <option value="c1">C1</option>
            <option value="c2">C2</option>
        </select>
        <h1>{language}</h1>
        {content}
    </>
}