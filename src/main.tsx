// index.tsx or main.tsx
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import LandingPage from './Components/LandingPage';
import UserHome from './Components/UserHome';
import Feed from './Components/Feed';
import PlaylistCreation from './Components/PlaylistCreation';
import UserPlaylists from './Components/UserPlaylists';

const router = createBrowserRouter([
  {
    path: '/',
    element: <LandingPage />
  },
  {
    path: 'Home',
    element: <UserHome />
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
