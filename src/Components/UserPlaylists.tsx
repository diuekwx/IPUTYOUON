import { useState, useEffect } from 'react'
import PlaylistEmbed from './PlaylistEmbed';

export default function UserPlaylists() {
    const [playlistIds, setPlaylistIds] = useState([]);

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
            }
        };

        getUserPlaylist();
    }, []);

    return (
        <div>
            <h2>Your Playlists</h2>
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