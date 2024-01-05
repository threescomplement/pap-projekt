import {useNavigate, useParams} from "react-router-dom";
import {ChangeEvent, useEffect, useState} from "react";
import {Course, CourseService} from "../../lib/Course";
import "./ReviewForm.css"
import {Review, ReviewRequest, ReviewService} from "../../lib/Review";
import useUser from "../../hooks/useUser";

export function ReviewForm() {
    const {courseId} = useParams();
    const username = useUser().user!.username;
    const [course, setCourse] = useState<Course | null>(null);
    const [rating, setRating] = useState<number | null>(null);
    const [opinion, setOpinion] = useState<string>("");
    const [previousReview, setPreviousReview] = useState<Review | null>(null);
    const navigate = useNavigate();


    useEffect(() => {
        CourseService.fetchCourse(courseId!)
            .then(c => {
                setCourse(c);
            })

        ReviewService.fetchReviewByCourseIdAndAuthor(courseId!, username)
            .then(r => setPreviousReview(r))
    }, [courseId, username]);

    useEffect(() => {
        if (previousReview !== null) {
            setOpinion(previousReview.opinion);
            setRating(Number(previousReview.overallRating));
        }
    }, [previousReview]);

    function RatingSlider() { // todo: swap this for a nicer one
        const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
            const value = Math.round(parseFloat(e.target.value));
            setRating(value);
        };

        return (
            <input
                type="range"
                min={1}
                max={10}
                step={1}
                value={rating != null ? rating : 1}
                onChange={handleChange}
                onInput={handleChange}
            />
        );
    }

    function handleClick() {
        if (rating == null) return; // todo: indicate to the user that they need to rate numerically
        const request: ReviewRequest = {
            text: opinion,
            rating: rating
        }
        ReviewService.postReview(request, courseId!);
        navigate(`/courses/${courseId}/thankyou`);
        // todo: remove this page from history so that clicking back doesn't return to it
    }

    return <div className="review-form">
        <h1>{previousReview === null ? "Napisz opinię" : "Edytuj swoją opinię"}
            {course !== null && (`do kursu ${course.name}`)}</h1>`
        <textarea placeholder="Co spodobało ci się w kursie, a co należy poprawić?"
                  onChange={e => setOpinion(e.target.value)}
                  value={opinion}>
        </textarea>
        <p>{rating !== null ? `Twoja ocena: ${rating}` : "Wybierz ocenę:"} </p>
        <div><RatingSlider/></div>
        <div>
            <button onClick={handleClick}>Zatwierdź</button>
        </div>
    </div>
}

