import { useState, useEffect } from 'react'
import PlaylistEmbed from './PlaylistEmbed';
import cd_disk from '../assets/cd.png';
import './LoadingScreen.css';
import './UserPlaylists.css';
import HomeButton from './HomeButton.tsx';

export default function UserPlaylists() {
    const [playlistIds, setPlaylistIds] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const getUserPlaylist = async () => {
            try {
                const response = await fetch('http://127.0.0.1:8080/api/playlist/get-user-playlist', {
                    credentials: 'include',
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                });

                console.log("Response status:", response.status);

                if (response.ok) {
                    const data = await response.json();
                    setPlaylistIds(data);
                    console.log("Playlist API data:", data);
                }
            } catch (err) {
                console.error("Failed to fetch playlists:", err);
            } finally {
                setLoading(false);
            }
        };

        getUserPlaylist();
    }, []);

    if (loading) {
        return (
            <div className="loading-screen">
                <img src={cd_disk} className="loading-spin"></img>
                <p>Loading...</p>
            </div>
        );
    }

    return (
        <div className="user-playlists">
            <HomeButton />
            <h1>Your Playlists</h1>
            {playlistIds.length > 0 ? (
                <PlaylistEmbed listOfPlaylist={playlistIds} />
            ) : (
                <p>No playlists found.</p>
            )}
        </div>
    );
}