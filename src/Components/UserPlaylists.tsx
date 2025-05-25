import { useState, useEffect } from 'react'
import PlaylistEmbed from './PlaylistEmbed';
import cd_disk from '../assets/cd.png';
import './LoadingScreen.css';

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
        <div>
            <h1 style={{
                color: 'white',
                fontSize: '50px',
                width: '100%',
                textAlign: 'center',
                lineHeight: '90px'
            }}><b>Your Playlists</b></h1>
            {playlistIds.length > 0 ? (
                <div>
                    <PlaylistEmbed listOfPlaylist={playlistIds} />
                </div>
            ) : (
                <p>No playlists found.</p>
            )}
        </div>
    );
}