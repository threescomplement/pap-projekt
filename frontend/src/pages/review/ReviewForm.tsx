import {useNavigate, useParams} from "react-router-dom";
import React, {ChangeEvent, useEffect, useState} from "react";
import {Course, CourseService} from "../../lib/Course";
import "./ReviewForm.css"
import {Review, ReviewRequest, ReviewService} from "../../lib/Review";
import useUser from "../../hooks/useUser";
import {ratingToPercentage} from "../../lib/utils";

export function ReviewForm() {
    const {courseId} = useParams();
    const username = useUser().user!.username;
    const [course, setCourse] = useState<Course | null>(null);
    const [easeRating, setEaseRating] = useState<number | null>(null);
    const [interestRating, setInterestRating] = useState<number | null>(null);
    const [engagementRating, setEngagementRating] = useState<number | null>(null);
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
            setEaseRating(Number(previousReview.easeRating));
            setInterestRating(Number(previousReview.interestRating));
            setEngagementRating(Number(previousReview.engagementRating));
        }
    }, [previousReview]);

    interface RatingSliderProps {
        rating: number | null;
        setRating: React.Dispatch<React.SetStateAction<number | null>>;
    }

    function RatingSlider({rating, setRating}: RatingSliderProps) { // todo: swap this for a nicer one
        const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
            const value = Math.round(parseFloat(e.target.value));
            setRating(value);
        };

        return (
            <input
                type="range"
                min={0}
                max={10}
                step={1}
                value={rating != null ? rating : 0}
                onChange={handleChange}
                onInput={handleChange}
            />
        );
    }

    function handleClick() {
        if ([easeRating, interestRating, engagementRating].some(r => r == null)) return; // todo: indicate to the user that they need to rate numerically
        console.log(easeRating)
        console.log(interestRating)
        console.log(engagementRating)
        const request: ReviewRequest = {
            text: opinion,
            easeRating: easeRating!,
            interestRating: interestRating!,
            engagementRating: engagementRating!
        }
        previousReview === null ? ReviewService.postReview(request, courseId!) : ReviewService.editReview(request, courseId!, username);
        navigate(`/courses/${courseId}/thankyou`);
        // todo: remove this page from history so that clicking back doesn't return to it
    }

    return <div className="review-form">
        <h1>{previousReview === null ? "Napisz opinię" : "Edytuj swoją opinię"}
            {course !== null && (` do kursu ${course.name}`)}</h1>`
        <textarea placeholder="Co spodobało ci się w kursie, a co należy poprawić?"
                  onChange={e => setOpinion(e.target.value)}
                  value={opinion}>
        </textarea>
        <div>
            <p>Jak prosty był kurs?</p>
            <div>
                <RatingSlider rating={easeRating} setRating={setEaseRating}/>
                {easeRating == null ? <p>Wybierz ocenę</p> : <p>Twoja ocena: {ratingToPercentage(easeRating)}</p>}
            </div>
        </div>
        <div>
            <p>Jak bardzo Cię zainteresował?</p>
            <RatingSlider rating={interestRating} setRating={setInterestRating}/>
            {interestRating == null ? <p>Wybierz ocenę</p> : <p>Twoja ocena: {ratingToPercentage(interestRating)}</p>}
        </div>
        <div>
            <p>Jak bardzo angażujący był?</p>
            <RatingSlider rating={engagementRating} setRating={setEngagementRating}/>
            {engagementRating == null ? <p>Wybierz ocenę</p> : <p>Twoja ocena: {ratingToPercentage(engagementRating)}</p>}
        </div>
        <div>
            <button onClick={handleClick}>Zatwierdź</button>
        </div>
    </div>
}

