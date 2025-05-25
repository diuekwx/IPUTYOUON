import { useState, useEffect, use } from 'react'
import PlaylistCreation from './PlaylistCreation';
import PlaylistEmbed from './PlaylistEmbed';
import { useNavigate } from 'react-router-dom';
import cd_disk from '../assets/cd.png';
import spotify_logo from '../assets/spotify-logo.png';
import './UserHome.css';
import './LoadingScreen.css';


export default function UserHome() {
    const [showCreation, setShowCreation] = useState(false);
    const [showPlaylist, setShowPlaylist] = useState(false);
    const [playListLink, setplayListLink] = useState([]);
    const navigate = useNavigate();
    const [loggedIn, setLoggedIn] = useState(false);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchUser = async () => {
            try {
                const res = await fetch("http://127.0.0.1:8080/api/spotify/me", {
                    credentials: "include",
                });

                if (res.ok) {
                    setLoggedIn(true);
                    const data = await res.text();
                    console.log("User is logged in", data);
                } else {
                    setLoggedIn(false);
                    console.log("User is not logged in");
                }
            } catch (error) {
                console.error("Failed to fetch user:", error);
                setLoggedIn(false);
            } finally {
                setLoading(false);
            }
        };

        fetchUser();
    }, []);

    // useEffect(() => {

    //     const fetchPlaylist = async () => {

    //     }
    // })

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

    // adds a little buffer time so the page properly loads
    if (loading) {
        return (
            <div className="loading-screen">
                <img src={cd_disk} className="loading-spin"></img>
                <p>Loading...</p>
            </div>
        );
    }

    return (
        <>
            <img src={cd_disk} className="cd-spin"></img>
            <div className="homepage">
                <h1><b>I PUT<br></br>YOU ON</b></h1>
                <div className="homepage-buttons">
                    <p> <br></br>tired of ur bum ass friends and their sh*t music?<br></br>
                        let itnernet strangers who indludge in the msot obscure genres put you on!@!!</p>

                    {loggedIn ? (
                        <div>
                            <button className="button1" onClick={() => navigate('/UserPlaylists')}>YOUR PLAYLISTS</button>
                            <button className="button2" onClick={() => navigate('/PlaylistCreation')}>CREATE NEW PLAYLIST</button>
                            <button className="button3" onClick={() => navigate('/Feed')}>COMMUNITY FEED</button>
                        </div>
                    ) : (
                        <div>
                            <button className="login-button" onClick={() => window.location.href = 'http://127.0.0.1:8080/oauth2/authorization/spotify'}
                            >Log In<img src={spotify_logo} width="30px"></img></button>
                        </div>
                    )}
                </div>
            </div>

        </>

    )

}