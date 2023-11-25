import {User} from "./User";
import {authHeader} from "./utils";

export interface Course {
    id: string,
    name: string,
    language: string,
    type: string | null, // TODO: can type be null?
    level: string | null, // TODO: can level be null?
    module: string | null, //TODO: make api return teacher id
    links: any
}

/**
 * Request the data of a course
 * @param courseId - id of the course
 */
export async function fetchCourse(courseId: string, user: User): Promise<Course> {
    // TODO: courseId does not exist in the database
    const response = await fetch(`${process.env.REACT_APP_API_ROOT}courses/${Number(courseId)}`, {
        headers: authHeader(user)
    });
    return await response.json();
}

export async function fetchCoursesByName(name: string, user: User): Promise<Course[]> {
    const response = await fetch(`${process.env.REACT_APP_API_ROOT}courses/search/findCoursesByNameContaining?name=${name}`, {
        headers: authHeader(user)
    });
    const json =  await response.json();
    return json._embedded.courses;

}