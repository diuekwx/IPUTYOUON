import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

export default function SpotifyRedirectHandler() {
    const navigate = useNavigate();

    useEffect(() => {
        navigate('/Home', { replace: true });
    }, [navigate]);

    return (
        <div>
            <p>Logging you in...</p>
        </div>
    );
}