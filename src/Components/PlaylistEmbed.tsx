interface PlaylistEmbedProps {
    listOfPlaylist: string[]; // ✔️ use lowercase string[]
}

export default function PlaylistEmbed({ listOfPlaylist }: PlaylistEmbedProps) {
    const url = `https://open.spotify.com/embed/playlist/`
    console.log(listOfPlaylist)
    return (

        <div className="playlist-embeds" style={{
            display: 'flex',
            flexWrap: 'wrap',
            justifyContent: 'space-around',
            alignItems: 'flex-start',
            rowGap: '30px',
            paddingLeft: '10%',
            paddingRight: '10%'
        }}>
            {listOfPlaylist.map((playlist: string, index: number) => (
                <iframe
                    key={index}
                    style={{ borderRadius: "12px" }}
                    src={url + playlist}
                    width="385px"
                    height="380"
                    allow="autoplay; clipboard-write; encrypted-media; fullscreen; picture-in-picture"
                    loading="lazy"
                    title={`Spotify playlist ${index}`}
                ></iframe>
            ))}
        </div>
    );
}
