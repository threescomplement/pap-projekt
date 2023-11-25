import {ICourse} from "../lib/Course";

interface CourseDataTableElementProps {
    label: string;
    value: string | null;
}

function CourseDataTableElement(element: CourseDataTableElementProps) {
    if (element.value == null || ["_links", "id"].includes(element.label)) {
        return null;
    }
    return <tr>
        <td>{element.label}: {element.value}</td>
    </tr>
}

export default function CourseData(course: ICourse) {
    const tableEntries = Object.entries(course).map(([label, value]) =>
        <CourseDataTableElement label={label} value={value}/>)
    return <>
        <h1>{course.name}</h1>
        <table>
            {tableEntries}
        </table>
    </>
}