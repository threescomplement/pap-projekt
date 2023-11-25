import {Course} from "../lib/Course";

interface CourseDetailsTableElementProps {
    label: string;
    value: string | null;
}

function CourseDetailsTableElement(element: CourseDetailsTableElementProps) {
    if (element.value == null || ["_links", "id"].includes(element.label)) {
        return null;
    }
    return <tr>
        <td>{element.label}: {element.value}</td>
    </tr>
}

export default function CourseDetails(course: Course) {
    const tableEntries = Object.entries(course).map(([label, value]) =>
        <CourseDetailsTableElement label={label} value={value}/>)
    return <>
        <h1>{course.name}</h1>
        <table>
            {tableEntries}
        </table>
    </>
}