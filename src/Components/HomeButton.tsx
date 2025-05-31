import { useNavigate } from 'react-router-dom';
import home from '../assets/home.png';
import './HomeButton.css';

export default function HomeButton() {
    const navigate = useNavigate();

    return (
        <button className="home-button" onClick={() => navigate('/')}><img src={home}></img></button>
    )
}