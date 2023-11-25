import {ITeacher} from "../lib/Teacher";

interface TeacherListProps {
    teachers: ITeacher[]
}

export default function TeacherList({teachers}: TeacherListProps) {
    return <table>
        {teachers.map(t => <Teacher teacher={t}/>)}
    </table>;
}


interface TeacherProps {
    teacher: ITeacher
}

export function Teacher({teacher}: TeacherProps) {
    return <tr>
        <td>{teacher.name}</td>
    </tr>
}