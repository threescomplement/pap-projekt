import React from 'react';
import logo from './logo.svg';
import './App.css';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import {Layout} from "./pages/Layout";
import {Home} from "./pages/Home";
import {Courses} from "./pages/Courses";
import {Teachers} from "./pages/Teachers";
import {NoPage} from "./pages/NoPage";
import {Login} from "./pages/Login";
import SingleCourse from "./pages/SingleCourse";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Layout/>}>
          <Route index element={<Home/>}/>
          <Route path="courses" element={<Courses/>}/>
          <Route path="courses/:courseId" element={<SingleCourse/>}/>
          <Route path="teachers" element={<Teachers/>}/>
          <Route path="login" element={<Login/>}/>
          <Route path="*" element={<NoPage/>}/>
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
