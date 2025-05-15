import { useState, useEffect } from "react";
import { useSearchParams } from 'react-router-dom';

export default function PlaylistCreation() {
    const [status, setStatus] = useState<string>("");
    const [name, setName] = useState('');
    const [desc, setDesc] = useState('');
    const [searchParams] = useSearchParams();
    const sessionId = searchParams.get('session');
    

    const printStuff = () => {
        console.log('Name:', name);
        console.log('Desc:', desc);

    }

    const createPlaylistz = async () => {
        try {
            setStatus("Creating playlist...");
            const response = await fetch('http://127.0.0.1:8080/api/playlist/create', {
                credentials: "include",
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    name: `${name}`,
                    description: `${desc}`,
                }),
            });
            
            // Log complete response for debugging
            console.log("Response status:", response.status);
            console.log("Response headers:", [...response.headers.entries()]);
            
            if (response.ok) {
                const data = await response.json();
                setStatus("Success! Playlist ID: " + data);
            } else {
                const errorText = await response.text();
                console.error("Error response:", errorText);
                setStatus(`Error ${response.status}: ${errorText}`);
            }
        } catch (err) {
            console.error("Network error:", err);
            setStatus(`Network error: ${err instanceof Error ? err.message : String(err)}`);
        }
    };


    
    return (
        <div>
            <h3>Spotify Playlist Creation</h3>

            <button onClick={createPlaylistz}>Create Playlist</button>

            <div>
                <textarea id="name"
                value={name}
                onChange={(e) => setName(e.target.value)}>
                </textarea>
                <textarea id="description"
                value={desc}
                onChange={(e) => setDesc(e.target.value)}>
                </textarea>

            </div>
            
            


        </div>
    );
}