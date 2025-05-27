import { useNavigate } from 'react-router-dom';
import home from '../assets/home.png';

export default function HomeButton() {
    const navigate = useNavigate();

    return (
        <button style={{
            background: 'none',
            border: 'none',
            position: 'absolute',
            width: '50px',
            height: '50px',
            top: '15px',
            left: '15px',
            padding: 0
        }} onClick={() => navigate('/')}><img src={home}></img></button>
    )
}