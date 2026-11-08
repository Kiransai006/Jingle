import { useState } from 'react';

// Shows the dish photo; if the remote image can't load, falls back to a styled tile with the name.
export default function FoodImage({ src, alt, className = '' }) {
  const [failed, setFailed] = useState(!src);

  if (failed) {
    return (
      <div className={`img-fallback ${className}`} role="img" aria-label={alt}>
        <span>{alt}</span>
      </div>
    );
  }
  return (
    <img
      src={src}
      alt={alt}
      className={className}
      loading="lazy"
      referrerPolicy="no-referrer"
      onError={() => setFailed(true)}
    />
  );
}
