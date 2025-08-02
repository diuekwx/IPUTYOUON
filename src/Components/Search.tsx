import { useState, useEffect } from 'react';
import './Search.css';
import debounce from './Debounce';
import { API_BASE_URL } from '../api/api';

interface SearchProps {
  selectedPlaylist: string | null;
  refreshState: boolean | null;
  refreshSearch: () => void;
  iframeRefresh: () => void;
}

export default function Search({ selectedPlaylist, refreshState, refreshSearch, iframeRefresh}: SearchProps) {
  const [status, setStatus] = useState<string>("Recommend");
  const [selectedTrack, setSelectedTrack] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [searchResults, setSearchResults] = useState<string[]>([]);
  const debouncedQuery = debounce(searchTerm, 200);

  const handleSelect = async (track: string) => {
    setSelectedTrack(track);
    const fullTrack = "spotify:track:" + track;

    // add to playlist
    try {
      setStatus("Recommending...");
      const response = await fetch(`${API_BASE_URL}/api/playlist/${selectedPlaylist}/add-tracks`, {
        credentials: "include",
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: fullTrack,
      });

      if (response.ok) {
        setStatus("Recommended!");
        iframeRefresh();
      } 
      else {
        const errorText = await response.text();
        console.error("Error response:", errorText);
        setStatus("ERROR");
      }

      setTimeout(() => {
        setSelectedTrack(null);
        setStatus("Recommend");
      }, 1500);

    } catch (err) {
      console.error("Network error:", err);
      setStatus("ERROR");
      setTimeout(() => setStatus("Recommend"), 1500);
    }
  };



  const url = `https://open.spotify.com/embed/track/`

  const search = async (term: string) => {
    try {
      const response = await fetch(`${API_BASE_URL}/api/playlist/search?query=${encodeURIComponent(term)}`, {
        credentials: 'include',
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        const data: string[] = await response.json();
        setSearchResults(data);
        console.log(data);
      } else {
        console.error('Search failed:', response.status);
      }
    } catch (error) {
      console.error('Error during search:', error);
    }
  };

  // const handleSearch = (event: ChangeEvent<HTMLInputElement>) => {
  //   const term = event.target.value;
  //   console.log("Search term:", term);
  //   setSearchTerm(term);

  //   if (term.trim() !== '') {
  //     search(term);
  //   } else {
  //     setSearchResults([]);
  //   }
  // };
  useEffect(() => {

    if (debouncedQuery.trim() === '') {
      setSearchResults([]);
      return;
    }
    if (debouncedQuery) {
      search(debouncedQuery);
    }
  }, [debouncedQuery]);

  useEffect(() => {
    if (refreshState) {
      setSearchResults([]);
      setSearchTerm('');
      refreshSearch();
    }
  }, [refreshSearch])

  return (
    <>
      <input
        type="text"
        placeholder="🔍 search for a song to recommend..."
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
      />
      <div>
        {searchResults.map((link: string, index: number) => (
          <div key={index} className="suggestion-card">
            <iframe
              src={url + link}
              height="80"
              allow="encrypted-media"
              className="song-searches"
            ></iframe>
            <button onClick={() => handleSelect(link)} className="green-button">{selectedTrack === link ? status : "Recommend"}</button>
          </div>
        ))}

      </div>
    </>

  );
}
