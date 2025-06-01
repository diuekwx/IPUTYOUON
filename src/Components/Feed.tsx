import { useState, useEffect } from 'react';
import Search from './Search';
import './Feed.css';
import HomeButton from './HomeButton.tsx';


export default function Feed() {
    const [selectedPlaylist, setSelectedPlaylist] = useState<string | null>("initialize");
    const [nextPlaylist, setNextPlaylist] = useState<string | null>(null);
    const [animationState, setAnimationState] = useState<'idle' | 'out' | 'in'>('idle');
    const [contributorsList, setContributorsList] = useState<{ username: string, contribution: string }[] | null>(null);

    useEffect(() => {
        // initialize feed page when you first go to it
        getFeed();
    }, []);

    useEffect(() => {
        if (animationState === 'out' && selectedPlaylist === nextPlaylist) {
            setAnimationState('in');
        }
    }, [selectedPlaylist, animationState, nextPlaylist]);

    const url = `https://open.spotify.com/embed/playlist/`

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
            console.log("Type:" + typeof data, Array.isArray(data), data)
            setContributorsList(data);
        }
    }

    const closeContributors = async () => {
        setContributorsList(null);
    }

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

            if (selectedPlaylist == "initialize") {
                // initialize selected playlist
                setSelectedPlaylist(data[Math.floor(Math.random() * data.length)]);
            }
            else {
                setNextPlaylist(data[Math.floor(Math.random() * data.length)]);
                setAnimationState('out');
            }


        }
    }

    const handleAnimationEnd = () => {
        if (animationState === 'out') {
            setSelectedPlaylist(nextPlaylist);
            //setAnimationState('in');
        } else if (animationState === 'in') {
            setAnimationState('idle');
        }
    };

    return (
        <div className="feed-page">
            <h1>COMMUNITY FEED</h1>

            {contributorsList ? (
                <div className="contributors-list" >
                    <ul>
                        {contributorsList.map((contributor, index) => (
                            <li key={index}>
                                - {contributor.username}: {contributor.contribution}
                            </li>
                        ))}
                    </ul>
                    <button onClick={closeContributors}>CLOSE</button>
                </div>
            ) : ''}

            <div className="track-card">
                <div className="feed-left">
                    {selectedPlaylist ? (
                        <iframe
                            className={`playlist ${animationState === 'out' ? 'slide-out' : animationState === 'in' ? 'slide-in' : ''}`}
                            onAnimationEnd={handleAnimationEnd}
                            style={{ borderRadius: "12px" }}
                            src={url + selectedPlaylist}
                            allow="autoplay; clipboard-write; encrypted-media; fullscreen; picture-in-picture"
                            loading="lazy"
                            title="Current/ Selected Playlist"
                        ></iframe>
                    ) : (
                        <div
                            className={`error-box ${animationState === 'out' ? 'slide-out' : animationState === 'in' ? 'slide-in' : ''}`}
                            onAnimationEnd={handleAnimationEnd}
                        ><p>Oops, an error occurred. Try refreshing again!</p>
                        </div>
                    )}

                    <div className="left-buttons">
                        <button onClick={getFeed} className="blue-button">Refresh</button>
                        <button onClick={() => getContributors()}
                            className={selectedPlaylist ? "pink-button" : "disabled-button"}
                            disabled={!selectedPlaylist}>
                            Get Contributors
                        </button>
                    </div>
                </div>

                <div className="feed-right">
                    <Search selectedPlaylist={selectedPlaylist} />


                </div>
            </div>

            <HomeButton />

        </div >
    )
}
