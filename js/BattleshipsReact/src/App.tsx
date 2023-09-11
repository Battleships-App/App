import * as React from 'react'
import { createBrowserRouter, Routes, Route, Link } from "react-router-dom";
import {Component, useEffect, useState} from "react";

import AuthService from "./services/user/auth.service";
import IUser from './types/user.type';

import Home from "./Components/Home/home.component"
import Login from "./Components/Login/login.component";
import Register from "./Components/SignUp/register.component";
import Profile from "./Components/Profile/profile.component";
import Board from "./Components/Leaderboard/leaderboard.component";
import {LobbyComponent} from "./Components/Lobby/lobby.component";
import Test2 from "./Components/Test/Test"


import "bootstrap/dist/css/bootstrap.min.css";
import EventBus from "./common/EventBus";

import "./App.css";
import {Game} from "./Components/Game/Game";
import infoService from "./services/info/info.service";

type Props = {};

const App = (props: Props) => {
    const [currentUser, setCurrentUser] = useState<IUser | undefined>(undefined);

    useEffect(() => {
        setCurrentUser(AuthService.getCurrentUser())
    }, []);

    return (
        <div>
            <div className="w3-top">
                {/*
                    <nav className="w3-row w3-padding w3-black">
                        <Link to={"/"} className="navbar-brand">
                            Battleships
                        </Link>
                        <div className="w3-col s3">
                            <a href="#" className="w3-button w3-block w3-black">HOME</a>
                        </div>
                        <div className="w3-col s3">
                            <a href="#about" className="w3-button w3-block w3-black">ABOUT</a>
                        </div>
                        <div className="w3-col s3">
                            <a href="#menu" className="w3-button w3-block w3-black">RULES</a>
                        </div>
                        <div className="w3-col s3">
                            <a href="#where" className="w3-button w3-block w3-black">PLAY</a>
                        </div>
                    </nav>
                    */
                }

            </div>

            <nav className="navbar navbar-expand navbar-dark bg-dark">
                <Link to={"/"} className="navbar-brand">
                    Battleships
                </Link>
                <div className="navbar-nav mr-auto">

                    <li className="nav-item">
                        <Link to={"/leaderboard"} className="nav-link">
                            Leaderboard
                        </Link>
                    </li>

                    <li className="nav-item">
                        <Link to={"/test"} className="nav-link">
                            Test
                        </Link>
                    </li>

                </div>

                {currentUser ? (
                    <div className="navbar-nav ml-auto">

                        <li className="nav-item">
                            <Link to={"/play"} className="nav-link">
                                Play
                            </Link>
                        </li>

                        <li className="nav-item">
                            <a href="/login" className="nav-link" onClick={ () => AuthService.logout(setCurrentUser)}>
                                LogOut
                            </a>
                        </li>

                        <li className="nav-item">
                            <Link to={"/profile"} className="nav-link" id={"Avatar"}>
                                <img src="https://s3-us-west-2.amazonaws.com/harriscarney/images/120x120.png"
                                     width={"30"} height="30"/>
                                {currentUser.username}
                            </Link>
                        </li>

                    </div>
                ) : (
                    <div className="navbar-nav ml-auto">
                        <li className="nav-item">
                            <Link to={"/login"} className="nav-link">
                                Login
                            </Link>
                        </li>

                        <li className="nav-item">
                            <Link to={"/register"} className="nav-link">
                                Sign Up
                            </Link>
                        </li>

                    </div>
                )}
            </nav>
            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path="/home" element={<Home/>}/>
                <Route path="/login" element={<Login/>}/>
                <Route path="/profile" element={<Profile/>}/>
                <Route path="/register" element={<Register/>}/>
                <Route path="/play" element={<Lobby/>}/>
                <Route path="/test" element={<Test2/>}/>
                <Route path="/leaderboard" element={<LeaderBoard/>}/>
            </Routes>
        </div>
    )


}

function Lobby() {
    return (
        <div>
            <LobbyComponent/>
        </div>
    )
}

function LeaderBoard() {
    return (
        <div className='App' id="main">
            <Board></Board>
        </div>

    )
}

function Info() {
    const [info, setInfo] = useState({version: "No info", authors: [{idNumber: "No info", author: "No info"}]})

    infoService.getInfo(setInfo)

    const listAuthors = info.authors.map((author, index) =>
        <li key={author.idNumber}>{author.idNumber} {author.author}</li>
    );

    return (
        <>
            <h1>Info</h1>
            <div>
                <h3>Version</h3>
                {info.version}
                <h3>Authors</h3>
                <ul>{listAuthors}</ul>
            </div>
        </>
    )
}

export default App;