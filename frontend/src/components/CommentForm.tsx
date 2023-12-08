import {useParams} from "react-router-dom";
import React, {useState} from "react";
import {CommentRequest, CommentService} from "../lib/ReviewComment";

interface CommentFormProps {
    reloadFlag: boolean;
    reloadFlagSetter: React.Dispatch<React.SetStateAction<boolean>>;
}

export function CommentForm({reloadFlag, reloadFlagSetter}: CommentFormProps) {
    const {courseId, authorUsername} = useParams();
    const [newComment, setNewComment] = useState<string>("");

    function handleClick() {
        console.log(newComment)
        if (newComment == "") return; //todo: inform user comment can't be blank
        const request: CommentRequest = {
            text: newComment
        }
        CommentService.postComment(request, courseId!, authorUsername!)
            .then(_=>{
                reloadFlagSetter(!reloadFlag);
                setNewComment("");
            });

    }

    return <div className="add-comment-container">
            <textarea
                placeholder="Twój komentarz"
                onChange={e => setNewComment(e.target.value)}
                value={newComment}
            />
        <button onClick={handleClick}>Dodaj komentarz</button>
    </div>
}
