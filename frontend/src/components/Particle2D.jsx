import { useRef } from "react";
import * as THREE from "three";

const Particle2D = ({ position, color, mass, isBlackHole }) => {
  const ref = useRef();
  const scale = Math.max(0.1, Math.log10(mass + 1));
  const particleColor = new THREE.Color(color);

  return (
    <group position={[position[0], position[1], 0]}>
      {isBlackHole && (
        <mesh scale={[scale * 3, scale * 3, scale * 3]}>
          <circleGeometry args={[0.4, 32]} />
          <meshBasicMaterial color="white" transparent opacity={0.1} />
        </mesh>
      )}

      <mesh ref={ref} scale={[scale, scale, scale]}>
        <circleGeometry args={[0.1, 32]} />
        <meshBasicMaterial color={isBlackHole ? "black" : particleColor} />
      </mesh>
    </group>
  );
};


export default Particle2D;
