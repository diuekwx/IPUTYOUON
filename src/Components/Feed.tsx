import {useState} from 'react';
import Search from './Search';

export default function Feed(){
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
    const getFeed  = async () => {
        const response = await fetch(`http://127.0.0.1:8080/api/playlist/feed`, {
            credentials: "include",
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            },
        })
        console.log("Response status:", response.status);
        if (response.ok){
            const data = await response.json();
            setFeed(data);
            console.log("feed data", data);

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
        if (response.ok){
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
    return(
        <>
        <Search setTrack={setSelectedTrack}></Search>
        <button onClick={() => getFeed()}>test</button>
        <div>
            {feed.map((playlist: string, index: number) => (
                <div key={index} className="track-card"> 
                    <iframe
                    key={index}
                    style={{ borderRadius: "12px" }}
                    src={url+ playlist}
                    width="100%"
                    height="380"
                    allow="autoplay; clipboard-write; encrypted-media; fullscreen; picture-in-picture"
                    loading="lazy"
                    title={`Spotify playlist ${index}`} 
                >
                
                </iframe>
                <button onClick={() => handleSelect(playlist)}>Recommend</button>
                </div>
                
                
            ))}
        </div>

        <button onClick={() => addToPlaylist()}>Add to Playlist</button>
        <button onClick={() => getContributors()}>Get Contributors</button>
        </>
    )
}