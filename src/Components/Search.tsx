import { useState, ChangeEvent } from 'react';
import './Search.css';

interface SearchProps {
  selectedPlaylist: string | null;
}

export default function Search({ selectedPlaylist }: SearchProps) {
  const [status, setStatus] = useState<string>("Recommend");
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [searchResults, setSearchResults] = useState<string[]>([]);

  const handleSelect = async (track: string) => {
    const fullTrack = "spotify:track:" + track;

    // add to playlist
    try {
      setStatus("Recommending...");
      const response = await fetch(`http://127.0.0.1:8080/api/playlist/${selectedPlaylist}/add-tracks`, {
        credentials: "include",
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: fullTrack,
      });

      if (response.ok) {
        setStatus("Recommended!");
        setTimeout(() => setStatus("Recommend"), 1500);

      } else {
        const errorText = await response.text();
        console.error("Error response:", errorText);
        setStatus("An error occurred :(");
        setTimeout(() => setStatus("Recommend"), 1500);
      }
    } catch (err) {
      console.error("Network error:", err);
      setStatus("An error occurred :(");
      setTimeout(() => setStatus("Recommend"), 1500);
    }
  };

  const url = `https://open.spotify.com/embed/track/`

  const search = async (term: string) => {
    try {
      const response = await fetch(`http://127.0.0.1:8080/api/playlist/search?query=${encodeURIComponent(term)}`, {
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

  const handleSearch = (event: ChangeEvent<HTMLInputElement>) => {
    const term = event.target.value;
    setSearchTerm(term);

    if (term.trim() !== '') {
      search(term);
    } else {
      setSearchResults([]);
    }
  };

  return (
    <>
      <input
        type="text"
        placeholder="🔍 search for a song to recommend..."
        value={searchTerm}
        onChange={handleSearch}
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
            <button onClick={() => handleSelect(link)} className="green-button">{status}</button>
          </div>
        ))}

      </div>
    </>

  );
}
