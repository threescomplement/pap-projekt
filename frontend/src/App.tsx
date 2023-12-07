import React, {useState} from 'react';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import {Layout} from "./pages/Layout";
import {Home} from "./pages/Home";
import {Courses} from "./pages/courses/Courses";
import {Teachers} from "./pages/teachers/Teachers";
import {NoPage} from "./pages/NoPage";
import Login from "./pages/user/Login";
import SingleCourse from "./pages/courses/SingleCourse";
import SingleTeacher from "./pages/teachers/SingleTeacher";
import {getStoredUser, storeUser, User} from "./lib/User";
import {CurrentUserContext} from "./hooks/useUser";
import Register from "./pages/user/Register";
import Profile from "./pages/user/Profile";
import ConfirmEmail from "./pages/user/ConfirmEmail";
import AdminPanel from "./pages/admin/AdminPanel";
import {SingleReview} from "./pages/review/SingleReview";

/**
 * Top level component for the entire application
 *
 * Provide context - variables available to all components via hooks like `useUser`
 *
 * Define routes - which components correspond to subpages with given URLs
 *
 * @constructor
 */
export default function App() {
    const [user, setUser] = useState<User | null>(getStoredUser());

    function setAndStoreUser(user: User | null) {
        setUser(user);
        storeUser(user);
    }

    return (
        <CurrentUserContext.Provider value={{user, setUser: setAndStoreUser}}>
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<Layout/>}>
                        <Route index element={<Home/>}/>
                        <Route path="courses">
                            <Route index element={<Courses/>}/>
                            <Route path=":courseId">
                                <Route index element={<SingleCourse/>}/>
                                <Route path="reviews/:authorUsername" element={<SingleReview/>}/>
                            </Route>
                        </Route>
                        <Route path="teachers">
                            <Route index element={<Teachers/>}/>
                            <Route path=":teacherId" element={<SingleTeacher/>}/>
                        </Route>
                        <Route path="user">
                            <Route index element={<Profile user={user}/>}/>
                            <Route path="login" element={<Login/>}/>
                            <Route path="register" element={<Register/>}/>
                            <Route path="verify/:token" element={<ConfirmEmail/>}/>
                        </Route>
                        <Route path="admin">
                            <Route index element={<AdminPanel />}/>
                        </Route>
                        <Route path="*" element={<NoPage/>}/>
                    </Route>
                </Routes>
            </BrowserRouter>
        </CurrentUserContext.Provider>
    );
}
