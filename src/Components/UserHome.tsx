import {useState, useEffect} from 'react'
import PlaylistCreation from './PlaylistCreation';

export default function UserHome() {
    const [showCreation, setShowCreation] = useState(false);
    // useEffect(() => {
    //     const fetchUser = async () => {
    //         const res = await fetch("http://127.0.0.1:8080/api/me", {
    //             credentials: "include",
    //         });
    //         console.log("hello")
    //         if (res.ok) {
    //             const data = await res.text(); // or .json() if you return JSON
    //             console.log("User is logged in!", data);
    //         } else {
    //             console.log("Not logged in");
    //         }
    //     };
    
    //     fetchUser();
    // }, []);


    const getUserPlaylist = async () => {
        const response = await fetch('http://127.0.0.1:8080/api/playlist/get-user-playlist', {
            credentials: "include",
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            },
        })
        console.log("Response status:", response.status);
    };

    return (
        <>

        <button onClick={() => getUserPlaylist()}> GET USER PLAYLIST</button>
        <h1> TIRED OF UR BUM ASS FRIENDS AND THEIR SHIT MUSIC? </h1>
        <h1> LET ITNERNET STRANGERS WHO INDLUDGE IN THE MSOT OBSCURE GENRES PUT YOU ON!@!! </h1>
        <button onClick={() => setShowCreation(true)}> Create Playlist</button>
            {showCreation ? <PlaylistCreation/> : null}
        
        </>
    )

}