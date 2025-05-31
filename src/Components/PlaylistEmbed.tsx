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
            width: '100%',
            rowGap: '30px',
            paddingLeft: '5%',
            paddingRight: '5%',
            paddingBottom: '5%',
            boxSizing: 'border-box',
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
