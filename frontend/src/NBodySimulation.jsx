import React, { useEffect, useState } from "react";
import axios from "axios";
import { Canvas } from "@react-three/fiber";
import { Sphere } from "@react-three/drei";

const NBodySimulation = () => {
    const [particles, setParticles] = useState([]);

    useEffect(() => {
        axios.get("http://localhost:8080/api/simulation/init")
            .then(response => {
                console.log("Particles received:", response.data); // 🛠️ Debugging
                setParticles(response.data);
            })
            .catch(error => console.error("Error fetching particles", error));
    }, []);
    

    const updateSimulation = () => {
        axios.get("http://localhost:8080/api/simulation/step?dt=1")
            .then(response => setParticles(response.data));
    };

    return (
        <div>
            <button onClick={updateSimulation}>Step</button>
            <Canvas>
                {particles.map((p, index) => (
                    <Sphere key={index} args={[0.1, 32, 32]} position={[p.x / 100, p.y / 100, 0]}>
                        <meshStandardMaterial color="white" />
                    </Sphere>
                ))}
            </Canvas>
        </div>
    );
};

export default NBodySimulation;