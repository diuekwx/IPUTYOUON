import {useState, useEffect, use} from 'react'
import PlaylistCreation from './PlaylistCreation';
import PlaylistEmbed from './PlaylistEmbed';
import { useNavigate } from 'react-router-dom';


export default function UserHome() {
    const [showCreation, setShowCreation] = useState(false);
    const [showPlaylist, setShowPlaylist] = useState(false);
    const [playListLink, setplayListLink] = useState([]);
    const navigate = useNavigate();

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

    useEffect(() => {

        const fetchPlaylist = async() => {

        }
    })

    // get user playlist on load, if empty then display "empty" text, otherwise display the playlist
    // returns a list of playlists
    const getUserPlaylist = async () => {
        const response = await fetch('http://127.0.0.1:8080/api/playlist/get-user-playlist', {
            credentials: "include",
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            },
        })
        console.log("Response status:", response.status);
        if (response.ok){
            setShowPlaylist(true);
            const data = await response.json();
            setplayListLink(data);
        }
    };
    // this was for testing purposes
    // const clearBtn = async () => {
    //     const response = await fetch('http://127.0.0.1:8080/api/playlist/clear', {
    //             credentials: "include",
    //             method: 'POST',
    //             headers: {
    //                 'Content-Type': 'application/json'
    //             }
    //         });
    //     console.log("Response status:", response.status);
        
    // }

      const feed = () => {
        navigate('/feed'); 
    };

    return (
        <>

        <button onClick={() => getUserPlaylist()}> GET USER PLAYLIST</button>
        <h1> TIRED OF UR BUM ASS FRIENDS AND THEIR SHIT MUSIC? </h1>
        <h1> LET ITNERNET STRANGERS WHO INDLUDGE IN THE MSOT OBSCURE GENRES PUT YOU ON!@!! </h1>
        <button onClick={() => setShowCreation(true)}> Create Playlist</button>
        {showCreation ? <PlaylistCreation/> : null}
        {/* <button onClick={() => clearBtn()}>Clear</button> */}
        {showPlaylist ? <PlaylistEmbed listOfPlaylist={playListLink}/> : null}
        <button onClick={() => feed()}>FEED</button>
        </>
        
    )

}