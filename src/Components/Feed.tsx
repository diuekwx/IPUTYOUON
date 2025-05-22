import {useState} from 'react';

export default function Feed(){
    const [feed, setFeed] = useState([]);
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
    // retrieve some random playlist from the server and display it 
    // put all that shit in like a queue or something ...? maybe an array lol idk 
    return(
        <>
        <button onClick={() => getFeed()}>test</button>
        <div>
            {feed.map((playlist: string, index: number) => (
                <iframe
                    key={index}
                    style={{ borderRadius: "12px" }}
                    src={url+ playlist}
                    width="100%"
                    height="380"
                    allow="autoplay; clipboard-write; encrypted-media; fullscreen; picture-in-picture"
                    loading="lazy"
                    title={`Spotify playlist ${index}`} 
                ></iframe>
            ))}
        </div>
        </>
    )
}