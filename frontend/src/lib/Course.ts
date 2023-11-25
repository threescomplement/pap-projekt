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
export async function attemptCourseDataRequest(courseId: string) {
    // TODO: courseId does not exist in the database
    return await fetch(`${process.env.REACT_APP_API_ROOT}courses/${Number(courseId)}`)
        .then(response => response.json())
        .catch(e => console.error(e));
}
