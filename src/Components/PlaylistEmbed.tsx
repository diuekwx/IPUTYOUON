interface PlaylistEmbedProps {
    listOfPlaylist: string[]; // ✔️ use lowercase string[]
}

export default function PlaylistEmbed({ listOfPlaylist }: PlaylistEmbedProps) {
    const url = `https://open.spotify.com/embed/playlist/`
    console.log(listOfPlaylist)
    return (

        <div>
            {listOfPlaylist.map((playlist: string, index: number) => (
                <iframe
                    key={index}
                    style={{ borderRadius: "12px" }}
                    src={url+ playlist}
                    width="100%"
                    height="380"
                    allow="autoplay; clipboard-write; encrypted-media; fullscreen; picture-in-picture"
                    loading="lazy"
                    title={`Spotify playlist ${index}`} 
                ></iframe>
            ))}
        </div>
    );
}
