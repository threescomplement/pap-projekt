import {ICourse} from "./Course";

export interface ITeacher {
    id: string,
    name: string,
    _links: any
}


export async function attemptTeacherDataRequest(teacherId: string) {
    /**
     * Request the data of a teacher
     * @param teacherId - teacher's id
     * */
    // TODO: teacherId does not exist in the database
    return await fetch(`${process.env.REACT_APP_API_ROOT}teachers/${Number(teacherId)}`)
        .then(response => response.json())
        .catch(e => console.error(e));
}


export async function getTeacherCourses(teacherId: string): Promise<ICourse[]> {
    /**
     * Get the data of all teacher's courses
     * */
    const response = await fetch(`${process.env.REACT_APP_API_ROOT}teachers/${Number(teacherId)}/courses`);
    const json = await response.json();
    return json._embedded.courses;
}