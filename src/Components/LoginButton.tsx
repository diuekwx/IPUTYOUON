import { useState } from 'react';

export default function LoginButton() {
    const [isLoading, setIsLoading] = useState(false);
    
    const handleSpotifyLogin = () => {
        setIsLoading(true);
        
        // Redirect to your Spring backend endpoint with the correct path
        window.location.href = 'http://127.0.0.1:8080/oauth2/authorization/spotify';
        
        // The Spring controller will then redirect to Spotify's OAuth page

        // fetch("http://localhost:8080/api/spotify/me", {
        //     credentials: "include"

        // });
        // console.log("done")
    };

    
    
    return (
        <button 
            className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-700 transition duration-300"
            onClick={handleSpotifyLogin}
            disabled={isLoading}
        >
            {isLoading ? 'Connecting...' : 'Login with Spotify'}
        </button>
    );
}