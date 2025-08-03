import { useEffect } from "react";
import { API_BASE_URL } from "../api/api";
import { useNavigate } from "react-router-dom";
import cd_disk from '../assets/cd.png';

export default function HomePage(){
    const navigate = useNavigate();

    
        useEffect(() => {
            const fetchUser = async () => {
                try {
                    const res = await fetch(`${API_BASE_URL}/api/spotify/me`, {
                        credentials: "include",
                    });
                    console.log(res);
                    if (res.ok) {
                        
                        const data = await res.text();
                        console.log("User is logged in", data);
                    } else {
                        
                        console.log("User is not logged in");
                    }
                } catch (error) {
                    console.error("Failed to fetch user:", error);
                    
                }
            };
    
            fetchUser();
        }, []);

    return (
        <>
        <div>
            <img src={cd_disk} className="cd-spin"></img>
            <div className="homepage">
                <div className="homepage-buttons">
                    <button className="button1" onClick={() => navigate('/UserPlaylists')}>YOUR PLAYLISTS</button>
                    <button className="button2" onClick={() => navigate('/PlaylistCreation')}>CREATE NEW PLAYLIST</button>
                    <button className="button3" onClick={() => navigate('/Feed')}>COMMUNITY FEED</button>
                </div>
            </div>
            
        </div>
        
        </>
    )
    
}