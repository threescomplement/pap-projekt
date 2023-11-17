import React, {useState} from 'react';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import {Layout} from "./pages/Layout";
import {Home} from "./pages/Home";
import {Courses} from "./pages/Courses";
import {Teachers} from "./pages/Teachers";
import {NoPage} from "./pages/NoPage";
import Login from "./pages/user/Login";
import SingleCourse from "./pages/SingleCourse";
import {User} from "./lib/User";
import {CurrentUserContext} from "./hooks/useUser";
import Register from "./pages/user/Register";
import Profile from "./pages/user/Profile";
import ConfirmEmail from "./pages/user/ConfirmEmail";
import AdminPanel from "./pages/admin/AdminPanel";

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
    const [user, setUser] = useState<User | null>(null);

    return (
        <CurrentUserContext.Provider value={{user, setUser}}>
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<Layout/>}>
                        <Route index element={<Home/>}/>
                        <Route path="courses" element={<Courses/>}/>
                        <Route path="courses/:courseId" element={<SingleCourse/>}/>
                        <Route path="teachers" element={<Teachers/>}/>
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
