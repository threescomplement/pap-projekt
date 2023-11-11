import {createContext, useContext} from "react";
import {User} from "../lib/User";

export interface ICurrentUserContext {
    user: User | null,
    setUser: (user: User | null) => void,
}

export const CurrentUserContext = createContext<ICurrentUserContext | null>(null);

/**
 * Access the currently logged-in user
 *
 * When no user is logged-in, then user is null.
 * setUser changes the current user globally for the application
 */
export default function useUser() {
    return useContext(CurrentUserContext) as ICurrentUserContext;
}