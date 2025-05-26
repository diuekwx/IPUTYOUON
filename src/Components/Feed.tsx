import { useState } from 'react';
import Search from './Search';
import './Feed.css';


export default function Feed() {
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
            console.log("feed data", data);


            // set selected playlist to a random playlist from the data
            if (data.length > 0) {
                setSelectedPlaylist(data[Math.floor(Math.random() * data.length)]);
            } else {
                // edge case: no playlists are returned
                setSelectedPlaylist(null);
            }


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


    // retrieve some random playlist from the server and display it
    // put all that shit in like a queue or something ...? maybe an array lol idk
    return (
        <>
            <div className="feed-page">
                <h1>COMMUNITY FEED</h1>
                <button onClick={() => getFeed()} className="blue-button">Refresh</button>
                {selectedPlaylist ? (
                    <div className="track-card">
                        <iframe
                            style={{ borderRadius: "12px" }}
                            src={url + selectedPlaylist}
                            allow="autoplay; clipboard-write; encrypted-media; fullscreen; picture-in-picture"
                            loading="lazy"
                            title={`Selected Spotify playlist`}
                            className="playlist"
                        ></iframe>


                        <div className="feed-buttons">
                            <Search setTrack={setSelectedTrack} />
                            <button onClick={() => addToPlaylist()}
                                className={selectedTrack ? "pink-button" : "disabled-button"}
                                disabled={!selectedTrack}
                            >
                                Recommend Selected Song
                            </button>
                            <button onClick={() => getContributors()} className="pink-button">
                                Get Contributors
                            </button>
                        </div>


                    </div>


                ) : (
                    <p>Oops, an error occurred. Try refreshing again!</p>
                )}
            </div>
        </>
    )
}
