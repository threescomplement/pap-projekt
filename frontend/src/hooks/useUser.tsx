import {useContext} from "react";
import {CurrentUserContext, ICurrentUserContext} from "../App";

export default function useUser() {
    return useContext(CurrentUserContext) as ICurrentUserContext;
}