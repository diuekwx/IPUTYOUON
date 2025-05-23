import { useState, useEffect, use } from 'react'
import PlaylistCreation from './PlaylistCreation';
import PlaylistEmbed from './PlaylistEmbed';
import { useNavigate } from 'react-router-dom';
import cd_disk from '../assets/cd.png';
import spotify_logo from '../assets/spotify-logo.png';
import './UserHome.css';


export default function UserHome() {
    const [showCreation, setShowCreation] = useState(false);
    const [showPlaylist, setShowPlaylist] = useState(false);
    const [playListLink, setplayListLink] = useState([]);
    const navigate = useNavigate();

    // useEffect(() => {
    //     const fetchUser = async () => {
    //         const res = await fetch("http://127.0.0.1:8080/api/me", {
    //             credentials: "include",
    //         });
    //         console.log("hello")
    //         if (res.ok) {
    //             const data = await res.text(); // or .json() if you return JSON
    //             console.log("User is logged in!", data);
    //         } else {
    //             console.log("Not logged in");
    //         }
    //     };

    //     fetchUser();
    // }, []);

    useEffect(() => {

        const fetchPlaylist = async () => {

        }
    })

    // get user playlist on load, if empty then display "empty" text, otherwise display the playlist
    // returns a list of playlists
    const getUserPlaylist = async () => {
        const response = await fetch('http://127.0.0.1:8080/api/playlist/get-user-playlist', {
            credentials: "include",
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            },
        })
        console.log("Response status:", response.status);
        if (response.ok) {
            setShowPlaylist(true);
            const data = await response.json();
            setplayListLink(data);
        }
    };
    // this was for testing purposes
    // const clearBtn = async () => {
    //     const response = await fetch('http://127.0.0.1:8080/api/playlist/clear', {
    //             credentials: "include",
    //             method: 'POST',
    //             headers: {
    //                 'Content-Type': 'application/json'
    //             }
    //         });
    //     console.log("Response status:", response.status);

    // }

    const feed = () => {
        navigate('/feed');
    };

    return (
        <>
            <img src={cd_disk} className="cd-spin"></img>
            <div className="homepage">
                <h1><b>I PUT<br></br>YOU ON</b></h1>
                <div className="homepage-buttons">
                    <p> <br></br>tired of ur bum ass friends and their sh*t music?<br></br>
                        let itnernet strangers who indludge in the msot obscure genres put you on!@!!</p>
                    <button className="button1" onClick={() => getUserPlaylist()}>YOUR PLAYLISTS</button>
                    <button className="button2" onClick={() => navigate('/playlist-creation')}>CREATE NEW PLAYLIST</button>
                    {/*
                    <button className="button2" onClick={() => setShowCreation(true)}>CREATE NEW PLAYLIST</button>
                    {showCreation ? <PlaylistCreation/> : null}
                    {/* <button onClick={() => clearBtn()}>Clear</button> (THIS WAS ALSO A COMMENT)/}
                    {showPlaylist ? <PlaylistEmbed listOfPlaylist={playListLink}/> : null}
                    COMMENTED OUT BC THIS WAS THE OLD PLAYLIST CREATION BUTTON, DONT KNOW IF ITS IMPORTANT*/}
                    <button className="button3" onClick={() => feed()}>COMMUNITY FEED</button>
                </div>
            </div>
            <button className="login-button">Log In<img src={spotify_logo} width="30px"></img></button>
        </>

    )

}