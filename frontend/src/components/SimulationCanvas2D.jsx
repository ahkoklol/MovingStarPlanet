import React, { useEffect, useState } from "react";
import { Canvas, useFrame } from "@react-three/fiber";
import Particle2D from "./Particle2D";
import axios from "axios";

// Fonction pour générer une couleur aléatoire en HSL
const getRandomColor = () => {
  const hue = Math.floor(Math.random() * 360);
  return `hsl(${hue}, 100%, 60%)`;
};

// Fonction pour générer une particule avec une position, 
// une vitesse, une masse et une couleur aléatoires
const generateRandomParticle = (bounds) => ({
  position: [
    (Math.random() - 0.5) * bounds * 2, 
    (Math.random() - 0.5) * bounds * 2
  ],
  velocity: [
    (Math.random() - 0.5) * 0.5, 
    (Math.random() - 0.5) * 0.5
  ],
  mass: Math.random() * 5 + 1, // Masse aléatoire entre 1 et 6
  color: getRandomColor(),
});

// Fonction de détection des collisions entre les particules
const detectCollisions = (particles) => {
  const radius = 0.1;
  const minDistance = radius * 2;

  for (let i = 0; i < particles.length; i++) {
    for (let j = i + 1; j < particles.length; j++) {
      const dx = particles[j].position[0] - particles[i].position[0];
      const dy = particles[j].position[1] - particles[i].position[1];
      const distance = Math.sqrt(dx * dx + dy * dy);

      if (distance < minDistance) {
        // Échange des vitesses en cas de collision
        const tempVel = particles[i].velocity;
        particles[i].velocity = particles[j].velocity;
        particles[j].velocity = tempVel;
      }
    }
  }
};

// Composant gérant la simulation des particules
const ParticlesSimulation = ({ particles, setParticles, bounds, speedMultiplier, isPaused }) => {

  // Fonction appliquant la gravité du trou noir sur les particules
  const applyGravity = (particles, blackHole) => {
    const G = 0.05; // Constante gravitationnelle simulée
    return particles.map((particle) => {
      if (particle.isBlackHole) return particle; // Le trou noir ne bouge pas

      const dx = blackHole.position[0] - particle.position[0];
      const dy = blackHole.position[1] - particle.position[1];
      const distance = Math.sqrt(dx * dx + dy * dy);

      if (distance < 1) {
        return null; // La particule est absorbée par le trou noir
      }

      // Calcul de la force gravitationnelle
      const force = (G * blackHole.mass) / (distance * distance);
      const accelerationX = force * (dx / distance);
      const accelerationY = force * (dy / distance);

      return {
        ...particle,
        velocity: [
          particle.velocity[0] + accelerationX,
          particle.velocity[1] + accelerationY,
        ],
      };
    }).filter(Boolean); // Supprime les particules absorbées
  };

  // Mise à jour des particules à chaque frame
  useFrame(() => {
    if (isPaused) return;
  
    setParticles((prevParticles) => {
      let blackHole = prevParticles.find(p => p.isBlackHole);
      let otherParticles = prevParticles.filter(p => !p.isBlackHole);
  
      if (blackHole) {
        blackHole = { ...blackHole, position: [0, 0], velocity: [0, 0] };
        otherParticles = applyGravity(otherParticles, blackHole);
      }
  
      otherParticles = otherParticles.map((particle) => {
        let newX = particle.position[0] + particle.velocity[0] * speedMultiplier;
        let newY = particle.position[1] + particle.velocity[1] * speedMultiplier;
        let newVelX = particle.velocity[0];
        let newVelY = particle.velocity[1];
  
        // Gestion des rebonds sur les bords du canvas
        if (newX >= bounds || newX <= -bounds) newVelX = -newVelX;
        if (newY >= bounds || newY <= -bounds) newVelY = -newVelY;
  
        return {
          ...particle,
          position: [newX, newY],
          velocity: [newVelX, newVelY],
        };
      });
  
      detectCollisions(otherParticles);
  
      return blackHole ? [blackHole, ...otherParticles] : otherParticles;
    });
  });
  
  



  return (
    <>
      {particles.map((body, index) => (
        <Particle2D
          key={index}
          position={body.position}
          color={body.color}
          mass={body.mass}
          isBlackHole={body.isBlackHole || false}
          onClick={() => deleteParticle(body.position[0], body.position[1])}
        />
      ))}
    </>
  );
};


const applyGravity = (particles, blackHole) => {
  const G = 0.05;

  return particles
    .map((particle) => {
      if (particle.isBlackHole) return particle;

      const dx = blackHole.position[0] - particle.position[0];
      const dy = blackHole.position[1] - particle.position[1];
      const distance = Math.sqrt(dx * dx + dy * dy);

      if (distance < 0.2) {
        return null;
      }

      const force = (G * blackHole.mass) / (distance * distance);
      const accelerationX = force * (dx / distance);
      const accelerationY = force * (dy / distance);

      return {
        ...particle,
        velocity: [
          particle.velocity[0] + accelerationX,
          particle.velocity[1] + accelerationY,
        ],
      };
    })
    .filter(Boolean);
};


// Composant principal de la simulation
const SimulationCanvas2D = () => {
  const [bodies, setBodies] = useState([]);
  const [speedMultiplier, setSpeedMultiplier] = useState(0.1);
  const [isPaused, setIsPaused] = useState(false);
  const simulationBounds = 4;
  const [showDescription, setShowDescription] = useState(false);
  const [socket, setSocket] = useState(null);

  // Récupération des données initiales via API
  useEffect(() => {
    axios.get("http://localhost:8080/api/simulation/init")
      .then((res) => {
        setBodies(res.data); // On récupère directement les données du backend
      })
      .catch((error) => console.error("Erreur de récupération :", error));

      const ws = new WebSocket("ws://localhost:8080/ws/particles");

      ws.onopen = () => {
        console.log("Connexion WebSocket établie");
      };

      ws.onmessage = (event) => {
        try {
          setTimeout(() => {
            const updatedParticles = JSON.parse(event.data);
            setBodies(updatedParticles);
          }, 100); // Attendre 100ms pour s'assurer que le WebSocket a tout reçu
        } catch (error) {
          console.error("Erreur de parsing WebSocket :", error);
        }
      };
      

      ws.onclose = () => {
        console.log("Connexion WebSocket fermée");
      };

      setSocket(ws);

      return () => {
        ws.close();
      };
  }, []);
  
  

  const addParticles = async (count) => {
    const newParticles = Array.from({ length: count }, () => ({
      mass: Math.random() * 5 + 1,
      position: [(Math.random() - 0.5) * 10, (Math.random() - 0.5) * 10],
      velocity: [(Math.random() - 0.5) * 0.5, (Math.random() - 0.5) * 0.5],
    }));
  
    try {
      // Attendre que toutes les requêtes POST soient terminées
      await Promise.all(newParticles.map(particle =>
        axios.post("http://localhost:8080/api/simulation/create", particle)
      ));
  
      // Attendre un court instant pour permettre au backend de tout traiter
      await new Promise((resolve) => setTimeout(resolve, 100));
  
      // Vérifier via une requête GET si toutes les particules ont bien été enregistrées
      const response = await axios.get("http://localhost:8080/api/simulation/init");
  
      // Debugging : Vérifier si on reçoit bien toutes les particules
      console.log("Total particules après ajout :", response.data.length);
  
      // Mettre à jour l'affichage avec toutes les particules reçues
      setBodies(response.data);
    } catch (error) {
      console.error("Erreur lors de l'ajout des particules :", error);
    }
  };
  
  
  
  

  const deleteParticle = (x, y) => {
    if (socket) {
      socket.send(`delete:${x}:${y}`);
    }
  };
  
  
  const clearParticles = () => {
    axios.delete("http://localhost:8080/api/simulation/clear")
      .catch((error) => console.error("Erreur lors du nettoyage :", error));
  };
  
  

  const increaseSpeed = () => {
    setSpeedMultiplier((prev) => Math.min(prev + 0.05, 1));
  };

  const decreaseSpeed = () => {
    setSpeedMultiplier((prev) => Math.max(prev - 0.05, 0.01));
  };

  const togglePause = () => setIsPaused((prev) => !prev);

  const addCentralParticle = () => {
    setBodies((prevBodies) => {
      const alreadyExists = prevBodies.some(p => p.isBlackHole);
      if (alreadyExists) return prevBodies;
  
      const blackHole = {
        position: [0, 0],
        velocity: [0, 0],
        mass: 100,
        color: "black",
        isBlackHole: true,
      };
  
      return [...prevBodies, blackHole];
    });
  };
  
  
  
  const toggleDescription = () => {
    setShowDescription(prev => !prev);
  };


  return (
    <>
    
    <h1 className="h1-press-start-2p">Simulation N-Body</h1>



      <div className="particle-counter">NOMBRE DE PARTICULES : {bodies.length}</div>

      <div className="button-container">
        <button className="add-particle-btn" onClick={() => addParticles(1)}>+1</button>
        <button className="add-particle-btn" onClick={() => addParticles(10)}>+10</button>
        <button className="add-particle-btn" onClick={() => addParticles(100)}>+100</button>
        <button className="add-particle-btn" onClick={() => addParticles(1000)}>+1000</button>
      </div>

      <div className="description-toggle-button">
        <button className="description-btn" onClick={toggleDescription}>
          {showDescription ? "MASQUER" : "DESCRIPTION DU PROJET"}
        </button>
      </div>

      {showDescription && (
        <div className="description-container">
          <p>
            Ce projet est une simulation interactive N-Body en 2D, développée avec React Three Fiber et un backend en Java avec Gradle.
            Les particules évoluent sous l'effet de la gravité, rebondissent entre elles et sur les bords du canvas, tout en étant attirées 
            par un trou noir central, qui peut être ajouté dynamiquement. L'utilisateur peut ajouter des particules, contrôler la vitesse,
            mettre en pause la simulation et nettoyer le canvas. Le backend fournit des données initiales via une API, permettant d'étendre 
            la simulation avec des fonctionnalités avancées comme la persistance des données.
          </p>
          <p>
            Wayne & Sarah
          </p>
        </div>
      )}

      <div className="speed-controls">
        <button className="speed-btn" onClick={decreaseSpeed}>-</button>
        <span className="speed-label">VITESSE : {speedMultiplier.toFixed(2)}</span>
        <button className="speed-btn" onClick={increaseSpeed}>+</button>
      </div>

      <div className="pause-button">
        <button className="pause-btn" onClick={togglePause}>
          {isPaused ? "REPRENDRE" : "PAUSE"}
        </button>
      </div>

      <div className="center-particle-button">
        <button className="add-central-particle-btn" onClick={addCentralParticle}>
          AJOUTER UN TROU NOIR
        </button>
      </div>

    <div className="clear-canvas-button">
      <button className="clear-canvas-btn" onClick={clearParticles}>
        NETTOYER
      </button>
    </div>

      <div className="canvas-container">
        <Canvas
          orthographic
          camera={{ position: [0, 0, 1], zoom: 70 }}
          style={{ width: "100%", height: "100%" }}
          gl={{ preserveDrawingBuffer: true }}
          dpr={[1, 1]}
          events={false}
        >
          <color attach="background" args={["black"]} />
          {bodies.length > 0 && (
            <ParticlesSimulation
              particles={bodies}
              setParticles={setBodies}
              bounds={simulationBounds}
              speedMultiplier={speedMultiplier}
              isPaused={isPaused}
            />
          )}
        </Canvas>
      </div>
    </>
  );
};

export default SimulationCanvas2D;
