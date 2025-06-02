import { useState, useEffect } from 'react';
import Search from './Search';
import './Feed.css';
import HomeButton from './HomeButton.tsx';


export default function Feed() {
    const [selectedPlaylist, setSelectedPlaylist] = useState<string | null>("initialize");
    const [nextPlaylist, setNextPlaylist] = useState<string | null>(null);
    const [animationState, setAnimationState] = useState<'idle' | 'out' | 'in'>('idle');
    const [showContributors, setShowContributors] = useState<boolean | null>(true);
    const [contributorsList, setContributorsList] = useState<{ username: string, contribution: string }[] | null>(null);
    const [refreshSearch, setRefreshSearch] = useState<boolean | null>(false);
    const [iframeKey, setIframeKey] = useState<number>(0);


    const handleIframeRefresh = async () => {
        setIframeKey(prevKey => prevKey + 1);
        getContributors();
  }

    useEffect(() => {
        // initialize feed page when you first go to it
        getFeed();
    }, []);

    useEffect(() => {
        if (animationState === 'out' && selectedPlaylist === nextPlaylist) {
            setAnimationState('in');
        }
    }, [selectedPlaylist, animationState, nextPlaylist]);

    useEffect(() => {
        if (selectedPlaylist && selectedPlaylist !== "initialize") {
            getContributors();
            setShowContributors(true);
        }
    }, [selectedPlaylist]);

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

    const getFeed = async () => {
        setRefreshSearch(true);
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

    const resetRefresh = () => {
        setRefreshSearch(false);
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

            <div className="track-card">
                <div className="feed-left">
                    {selectedPlaylist ? (
                        <iframe
                            key={iframeKey}
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


                    {selectedPlaylist && (
                        showContributors ? (
                            <div className="contributors-box" >
                                <p>PLAYLIST CONTRIBUTORS</p>
                                <div className="contributors">
                                    {contributorsList ? (
                                        <table>
                                            <thead><tr>
                                                <th style={{ textAlign: "left" }}><u>Username</u></th>
                                                <th style={{ textAlign: "right" }}><u>Song</u></th>
                                            </tr></thead>
                                            <tbody>
                                                {contributorsList.map((contributor, index) => (
                                                    <tr key={index}>
                                                        <td style={{ textAlign: "left" }}>{contributor.username}</td>
                                                        <td style={{ textAlign: "right" }}>{contributor.contribution}</td>
                                                    </tr>))}
                                            </tbody>
                                        </table>
                                    ) : (
                                        <p>This playlist has no contributors.<br></br>Be the first!</p>
                                    )}
                                </div>
                                <button className="show-hide-contributors" onClick={() => setShowContributors(false)}><u>Hide Contributors</u></button>
                            </div>

                        ) : (
                            <div className="hidden-contributors">
                                <button className="show-hide-contributors" onClick={() => setShowContributors(true)}><u>Show Contributors</u></button>
                            </div>
                        )
                    )}

                    <div className="left-buttons">
                        <button onClick={getFeed} className="blue-button">Refresh</button>
                    </div>
                </div>

                <div className="feed-right">
                    <Search selectedPlaylist={selectedPlaylist} 
                    refreshState={refreshSearch} 
                    refreshSearch={() => resetRefresh()}
                    iframeRefresh={() => handleIframeRefresh()}/>
                
                </div>
            </div>

            <HomeButton />

        </div >
    )
}