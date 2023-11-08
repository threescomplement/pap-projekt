import React, {useState} from 'react';
import './App.css';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import {Layout} from "./pages/Layout";
import {Home} from "./pages/Home";
import {Courses} from "./pages/Courses";
import {Teachers} from "./pages/Teachers";
import {NoPage} from "./pages/NoPage";
import {Login} from "./pages/Login";
import SingleCourse from "./pages/SingleCourse";
import {User} from "./lib/User";
import {CurrentUserContext} from "./hooks/useUser";
import Register from "./pages/Register";
import Profile from "./pages/Profile";


function App() {
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
                        <Route path="login" element={<Login/>}/>
                        <Route path="register" element={<Register/>}/>
                        <Route path="profile" element={<Profile user={user}/>}/>
                        <Route path="*" element={<NoPage/>}/>
                    </Route>
                </Routes>
            </BrowserRouter>
        </CurrentUserContext.Provider>

    );
}

export default App;
