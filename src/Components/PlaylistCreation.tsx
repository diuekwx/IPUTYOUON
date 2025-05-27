import { useState } from "react";
import { useSearchParams } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';
import './PlaylistCreation.css';

export default function PlaylistCreation() {
    const [status, setStatus] = useState<string>("");
    const [name, setName] = useState('');
    const [desc, setDesc] = useState('');
    const [searchParams] = useSearchParams();
    const sessionId = searchParams.get('session');
    const navigate = useNavigate();


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

            // clear textarea inputs
            setName('');
            setDesc('');

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
        <div className="playlist-creation">
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
                <button onClick={createPlaylistz}>CREATE</button>
                <p>{status}</p>
            </div>


            <button className="cancel" onClick={() => navigate('/Home')}><b>X</b></button>

        </div>
    );
}