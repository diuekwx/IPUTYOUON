import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom';
import cd_disk from '../assets/cd.png';
import spotify_logo from '../assets/spotify-logo.png';
import './UserHome.css';
import './LoadingScreen.css';


export default function UserHome() {
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
                <h1>I PUT YOU ON</h1>
                <div className="homepage-buttons">
                    <p> <br></br>tired of ur bum ass friends and their sh*t music?
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