// index.tsx or main.tsx
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import UserHome from './Components/UserHome';
import Feed from './Components/Feed';
import PlaylistCreation from './Components/PlaylistCreation';
import UserPlaylists from './Components/UserPlaylists';
import SpotifyRedirectHandler from './Components/SpotifyRedirectHandler';
import HomePage from './Components/HomePage';

const router = createBrowserRouter([
  {
    path: '/',
    element: <UserHome />
  },
  {
    path:'/Home',
    element: <HomePage />
  },
  {
    path: '/login/oauth2/code/spotify', 
    element: <SpotifyRedirectHandler />
  },
  {
    path: 'Feed',
    element: <Feed />
  },
  {
    path: 'PlaylistCreation',
    element: <PlaylistCreation />
  },
  {
    path: 'UserPlaylists',
    element: <UserPlaylists />
  }
]);

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <RouterProvider router={router} />
  </StrictMode>
);
