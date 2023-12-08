import React, { useEffect } from "react";
import {useNavigate, useParams} from "react-router-dom";
import "./ThankYouPage.css";

export function ThankYouPage() {
    const navigate = useNavigate();
    const {courseId }= useParams();

    useEffect(() => {
        const redirectTimeout = setTimeout(() => {
            navigate("/courses/" + courseId);
        }, 3000);

        return () => clearTimeout(redirectTimeout);
    }, [navigate]);

    return (
        <div className="thank-you-page">
            <h1>Dziękujemy!</h1>
            <p>Twoja opinia została zarejestrowana.</p>
            <p>Wkrótce zostaniesz przekierowany...</p>
        </div>
    );
}
