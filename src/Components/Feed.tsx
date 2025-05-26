import { useState } from 'react';
import Search from './Search';
import './Feed.css';

export default function Feed() {
    const [feed, setFeed] = useState([]);
    const [selectedTrack, setSelectedTrack] = useState<string | null>(null);
    const [selectedPlaylist, setSelectedPlaylist] = useState<string | null>(null);

    const addToPlaylist = async () => {
        console.log("using:", selectedPlaylist)
        const response = await fetch(`http://127.0.0.1:8080/api/playlist/${selectedPlaylist}/add-tracks`, {
            credentials: "include",
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: selectedTrack,
        });

        if (response.ok) {
            const data = await response.json();
            console.log("Added track to playlist:", data);
        }
    }

    const url = `https://open.spotify.com/embed/playlist/`
    //continuiously add to array as they scroll ? 
    const getFeed = async () => {
        const response = await fetch(`http://127.0.0.1:8080/api/playlist/feed`, {
            credentials: "include",
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            },
        })
        console.log("Response status:", response.status);
        if (response.ok) {
            const data = await response.json();
            setFeed(data);
            console.log("feed data", data);

            // reset selected playlist to none
            setSelectedPlaylist(null);

        }
    }

    const getContributors = async () => {
        const response = await fetch(`http://127.0.0.1:8080/api/playlist/${selectedPlaylist}/contributors`, {
            credentials: "include",
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            },
        });
        if (response.ok) {
            const data = await response.json();
            console.log(data);
        }

    }

    const handleSelect = (playlist: string) => {
        console.log("playlist: ", playlist);
        setSelectedPlaylist(playlist);
    }
    // retrieve some random playlist from the server and display it 
    // put all that shit in like a queue or something ...? maybe an array lol idk 
    return (
        <>
            <div className="feed-page">
                <h1>COMMUNITY FEED</h1>
                <button onClick={() => getFeed()} className="blue-button">Refresh</button>
                {feed.map((playlist: string, index: number) => (
                    <div key={index} className="track-card">
                        <iframe
                            key={index}
                            style={{ borderRadius: "12px" }}
                            src={url + playlist}
                            allow="autoplay; clipboard-write; encrypted-media; fullscreen; picture-in-picture"
                            loading="lazy"
                            title={`Spotify playlist ${index}`}
                            className="playlist"
                        ></iframe>
                        <div className="feed-buttons">

                            {selectedPlaylist === playlist ? (
                                <div>
                                    <Search setTrack={setSelectedTrack} />
                                    <button onClick={() => addToPlaylist()}
                                        className={selectedTrack ? "pink-button" : "disabled-button"}
                                        disabled={!selectedTrack}
                                    >Recommend Selected Song</button>
                                    <button onClick={() => getContributors()} className="pink-button">Get Contributors</button>
                                </div>
                            ) : (
                                <button onClick={() => handleSelect(playlist)} className="pink-button">Add to this Playlist</button>
                            )}
                        </div>
                    </div>


                ))}
            </div>


        </>
    )
}