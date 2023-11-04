import {createContext, useContext} from "react";
import {User} from "../lib/User";

export interface ICurrentUserContext {
    user: User | null,
    setUser: (user: User) => void,
}

export const CurrentUserContext = createContext<ICurrentUserContext | null>(null);

export default function useUser() {
    return useContext(CurrentUserContext) as ICurrentUserContext;
}