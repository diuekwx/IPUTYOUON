import { useState, useEffect } from 'react';
import Search from './Search';
import './Feed.css';
import HomeButton from './HomeButton.tsx';


export default function Feed() {
    const [selectedTrack, setSelectedTrack] = useState<string | null>(null);
    const [selectedPlaylist, setSelectedPlaylist] = useState<string | null>(null);
    const [nextPlaylist, setNextPlaylist] = useState<string | null>(null);
    const [animationState, setAnimationState] = useState<'idle' | 'out' | 'in'>('idle');


    useEffect(() => {
        getFeed();
    }, []);

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
            // TODO: need to update playlistEmbed to show the added song
        }
    }

    const url = `https://open.spotify.com/embed/playlist/`

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
                if (selectedPlaylist == null) {
                    setSelectedPlaylist(data[Math.floor(Math.random() * data.length)]);
                }
                else {
                    setNextPlaylist(data[Math.floor(Math.random() * data.length)]);
                    setAnimationState('out');
                }
            } else {
                // edge case: no playlists are returned
                setSelectedPlaylist(null);
            }

        }
    }

    const handleAnimationEnd = () => {
        if (animationState === 'out') {
            setSelectedPlaylist(nextPlaylist);
            setAnimationState('in');
        } else if (animationState === 'in') {
            setAnimationState('idle');
        }
    };

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

    return (
        <div className="feed-page">
            <h1>COMMUNITY FEED</h1>

            {selectedPlaylist ? (
                <div className="track-card">
                    <div className="feed-left">
                        <iframe
                            className={`playlist ${animationState === 'out' ? 'slide-out' : animationState === 'idle' ? '' : 'hidden'}`}
                            onAnimationEnd={handleAnimationEnd}
                            style={{ borderRadius: "12px" }}
                            src={url + selectedPlaylist}
                            allow="autoplay; clipboard-write; encrypted-media; fullscreen; picture-in-picture"
                            loading="lazy"
                            title="Current/ Selected Playlist"
                        ></iframe>

                        <iframe
                            className={`playlist ${animationState === 'in' ? 'slide-in' : 'hidden'}`}
                            onAnimationEnd={handleAnimationEnd}
                            style={{ borderRadius: "12px" }}
                            src={url + nextPlaylist}
                            allow="autoplay; clipboard-write; encrypted-media; fullscreen; picture-in-picture"
                            loading="lazy"
                            title="Next Playlist"
                        ></iframe>
                        <button onClick={getFeed} className="blue-button">Refresh</button>
                    </div>

                    <div className="feed-right">
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
                <div className="track-card">
                    <div className="feed-left">
                        <div className="error-box"><p>Oops, an error occurred. Try refreshing again!</p></div>
                        <button onClick={() => getFeed()} className="blue-button">Refresh</button>
                    </div>

                    <div className="feed-right">
                        <Search setTrack={setSelectedTrack} />
                        <button onClick={() => addToPlaylist()}
                            className={selectedTrack ? "pink-button" : "disabled-button"}
                            disabled={!selectedTrack}
                        >
                            Recommend Selected Song
                        </button>
                        <button onClick={() => getContributors()} className="disabled-button">
                            Get Contributors
                        </button>
                    </div>
                </div>
            )}

            <HomeButton />

        </div>
    )
}
