import { useState, ChangeEvent } from 'react';

interface SearchProps {
  setTrack: React.Dispatch<React.SetStateAction<string | null>>;
}

export default function Search({setTrack}: SearchProps) {
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [searchResults, setSearchResults] = useState<string[]>([]);
  // const [selectedTrack, setSelectedTrack] = useState<string | null>(null);

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
        const data: string[] = await response.json(); // Adjust type if your backend returns more complex data
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
        placeholder="Search..."
        value={searchTerm}
        onChange={handleSearch}
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
            {
            searchResults.map((link: string, index: number) => (
                <div key={index} className="track-card">
                <iframe
                    src={url + link}
                    width="300"
                    height="80"

                    allow="encrypted-media"
                ></iframe>
                <button onClick={() => handleSelect(link)}>Recommend</button>
                </div>
            ))
            }

        </div>
    </>
    
  );
}
