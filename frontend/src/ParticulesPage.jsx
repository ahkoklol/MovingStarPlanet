import { useState, useEffect, useRef } from "react";
import "./ParticulesPage.scss";

const NUM_CIRCLES = 50; // Nombre de formes
const RADIUS = 300; // Taille de la rosace
const SPEED = 0.001; // Vitesse de mouvement

const ParticulesPage = () => {
  const [circles, setCircles] = useState(
    Array.from({ length: NUM_CIRCLES }, (_, i) => ({
      id: i,
      angle: (i * (2 * Math.PI)) / NUM_CIRCLES, // Décalage de phase pour chaque cercle
    }))
  );

  const [isAnimating, setIsAnimating] = useState(true); // État pour activer/désactiver l'animation
  const animationRef = useRef(null);

  useEffect(() => {
    if (!isAnimating) return; // Si l'animation est stoppée, on ne fait rien

    let startTime = performance.now();

    const animate = (time) => {
      if (!isAnimating) return; // Vérification à chaque frame

      const elapsedTime = (time - startTime) * SPEED;

      setCircles((prevCircles) =>
        prevCircles.map((circle) => {
          const newAngle = elapsedTime + circle.angle;

          const x = window.innerWidth / 2 + RADIUS * Math.cos(newAngle * 3) * Math.cos(newAngle);
          const y = window.innerHeight / 2 + RADIUS * Math.cos(newAngle * 3) * Math.sin(newAngle);

          return { ...circle, x, y };
        })
      );

      animationRef.current = requestAnimationFrame(animate);
    };

    animationRef.current = requestAnimationFrame(animate);

    return () => cancelAnimationFrame(animationRef.current);
  }, [isAnimating]);

  return (
    <div className="page-container">
      <button className="stop-button" onClick={() => setIsAnimating(!isAnimating)}>
        {isAnimating ? "Stopper" : "Reprendre"}
      </button>

      {circles.map((circle) => (
        <div
          key={circle.id}
          className="circle"
          style={{ left: `${circle.x}px`, top: `${circle.y}px` }}
        ></div>
      ))}
    </div>
  );
};

export default ParticulesPage;
