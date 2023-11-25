import {ICourse} from "../lib/Course";

interface CourseListProps {
    courses: ICourse[]
}

export default function CourseList({courses}: CourseListProps) {

    return <table>
        {courses.map(c => <Course course={c}/>)}
    </table>;
}


interface CourseProps {
    course: ICourse
}

export function Course({course}: CourseProps) {
    return <tr>
        <td>{course.name}</td>
    </tr>
}