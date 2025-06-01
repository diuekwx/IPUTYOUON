import { useState, ChangeEvent, useEffect } from 'react';
import './Search.css';
import debounce from './Debounce';

interface SearchProps {
  setTrack: React.Dispatch<React.SetStateAction<string | null>>;
  selectedTrack: string | null;
  refreshSearch: boolean | null;
  resetFunc: () => void;
}

export default function Search({ setTrack, selectedTrack, refreshSearch, resetFunc }: SearchProps) {
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [searchResults, setSearchResults] = useState<string[]>([]);
  const debouncedQuery = debounce(searchTerm, 200);


  const handleSelect = (track: string) => {
    setTrack("spotify:track:" + track);
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
    
    if (debouncedQuery.trim() === ''){
      setSearchResults([]);
      return;
    }
    if (debouncedQuery){
      search(debouncedQuery);
    }
  }, [debouncedQuery]);

  useEffect(() => {
      if (refreshSearch) {
      setSearchResults([]);
      setSearchTerm('');
      resetFunc();
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
        {/* {searchResults.map((link: string, index: number) => (
                <iframe
                    key={index}
                    style={{ borderRadius: "12px" }}
                    src={url+ link}
                    width="100%"
                    height="380"
                    allow="autoplay; clipboard-write; encrypted-media; fullscreen; picture-in-picture"
                    loading="lazy"
                    title={`Spotify playlist ${index}`} 
                ></iframe>
            ))} */}
        {searchResults.map((link: string, index: number) => (
          <div key={index} className="suggestion-card">
            <iframe
              src={url + link}
              height="80"
              allow="encrypted-media"
              className="song-searches"
            ></iframe>
            <button onClick={() => handleSelect(link)} className="green-button">{selectedTrack === `spotify:track:${link}` ? "Selected" : "Select"}</button>
          </div>
        ))}

      </div>
    </>

  );
}
