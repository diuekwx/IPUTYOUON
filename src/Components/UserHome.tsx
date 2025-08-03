
import cd_disk from '../assets/cd.png';
import spotify_logo from '../assets/spotify-logo.png';
import './UserHome.css';
import './LoadingScreen.css';
import { API_BASE_URL } from '../api/api';


export default function UserHome() {



    // adds a little buffer time so the page properly loads
    // if (loading) {
    //     return (
    //         <div className="loading-screen">
    //             <img src={cd_disk} className="loading-spin"></img>
    //             <p>Loading...</p>
    //         </div>
    //     );
    // }

    return (
        <>
            <img src={cd_disk} className="cd-spin"></img>
            <div className="homepage">
                <h1>I PUT YOU ON</h1>
                <div className="homepage-buttons">
                    <p> <br></br>tired of ur bum ass friends and their sh*t music?
                        let itnernet strangers who indludge in the msot obscure genres put you on!@!!</p>
                        <div>
                            <button className="login-button" onClick={() => window.location.href = `${API_BASE_URL}/oauth2/authorization/spotify`}
                            >Log In<img src={spotify_logo} width="30px"></img></button>
                        </div>

                </div>
            </div>

        </>

    )

}