import { useState } from "react";
import './PlaylistCreation.css';
import HomeButton from './HomeButton.tsx';
import { API_BASE_URL } from "../api/api.ts";

export default function PlaylistCreation() {
    const [status, setStatus] = useState<string>("CREATE");
    const [name, setName] = useState('');
    const [desc, setDesc] = useState('');


    const createPlaylistz = async () => {
        try {
            setStatus("Creating playlist...");
            const response = await fetch(`${API_BASE_URL}/api/playlist/create`, {
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

            // clear textarea inputs
            setName('');
            setDesc('');

            if (response.ok) {
                setStatus("CREATED!");
                setTimeout(() => {
                    setStatus('CREATE');
                }, 1500);

            } else {
                const errorText = await response.text();
                console.error("Error response:", errorText);
                //setStatus(`Error ${response.status}: ${errorText}`);
                setStatus("An error occured :(");
                setTimeout(() => {
                    setStatus('CREATE');
                }, 1500);
            }
        } catch (err) {
            console.error("Network error:", err);
            //setStatus(`Network error: ${err instanceof Error ? err.message : String(err)}`);
            setStatus("An error occured :(");
            setTimeout(() => {
                setStatus('CREATE');
            }, 1500);
        }
    };



    return (
        <div className="playlist-creation">
            <HomeButton />
            <h1>Spotify Playlist Creation</h1>

            <div>
                <textarea id="title"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    placeholder="type text here..."
                    rows={1}>
                </textarea>
                <p>title</p>
            </div>

            <div>
                <textarea id="description"
                    value={desc}
                    onChange={(e) => setDesc(e.target.value)}
                    placeholder="type text here..."
                    rows={1}>
                </textarea>
                <p>description</p>
            </div>

            <div>
                <button onClick={createPlaylistz}>{status}</button>
            </div>

        </div>
    );
}