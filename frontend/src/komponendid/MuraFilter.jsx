function MuraFilter() {
    return (
        <div className="noise-layer">
            <svg viewBox="0 0 100 100" preserveAspectRatio="none">
                <filter id="noiseFilter">
                    <feTurbulence type="fractalNoise" baseFrequency="20" numOctaves="10" stitchTiles="stitch"/>
                </filter>
                <rect width="100%" height="100%" filter="url(#noiseFilter)" />
            </svg>
        </div>
    )
}

export default MuraFilter;