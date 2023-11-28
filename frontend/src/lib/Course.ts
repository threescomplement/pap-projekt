import api from "./api";

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
export async function fetchCourse(courseId: string): Promise<Course> {
    // TODO: courseId does not exist in the database
    const response = await api.get(`/courses/${Number(courseId)}`);
    return await response.json();
}

export async function fetchCoursesByName(name: string): Promise<Course[]> {
    const response = await api.get(`/courses/search/findCoursesByNameContaining?name=${name}`);
    const json = await response.json();
    return json._embedded.courses;

}