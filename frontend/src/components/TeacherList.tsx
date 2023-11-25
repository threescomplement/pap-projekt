import {ITeacher} from "../lib/Teacher";
import React from "react";
import {Link} from "react-router-dom";

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
    // TODO: get teacher Id
    return <tr>
        <td><Link to="1"> {teacher.name}</Link></td>
    </tr>
}