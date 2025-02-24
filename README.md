# MovingStarPlanet

## Overview

**MovingStarPlanet** is an **N-Body Simulation** designed to predict the individual motions of celestial objects interacting gravitationally. This project aims to simulate the complex dynamics of bodies under mutual gravitational attraction, providing a visual and computational representation of orbital mechanics.

## What is the N-Body Problem?

The N-body problem involves calculating the motion of a group of celestial objects that influence each other through gravitational forces. Each object, or "body," is represented as a point mass with the following properties:
- **Position**: The location of the body in space.
- **Velocity**: The speed and direction of the body's movement.
- **Acceleration**: The change in velocity due to gravitational forces.
- **Mass**: The quantity of matter within the body, influencing its gravitational pull on other bodies.

## How It Works

The simulation manages a virtual universe where celestial bodies interact through gravity, governed by Newton's law of universal gravitation:

\[
F = G \frac{m_1 m_2}{r^2}
\]

Where:
- \( F \) = Gravitational force between two bodies
- \( G \) = Gravitational constant
- \( m_1, m_2 \) = Masses of the two bodies
- \( r \) = Distance between the centers of the two bodies

The positions and velocities of all bodies are updated iteratively to simulate their motion over time.

## Features

- Accurate gravitational calculations for N bodies
- Realistic simulation of celestial mechanics
- Configurable parameters for mass, velocity, and initial positions
- Interactive visualization of orbital dynamics

## Getting Started

### Prerequisites

Ensure you have the following installed on your system:
- [Node.js](https://nodejs.org) (for frontend)
- [Java](https://www.java.com) (for backend using Quarkus)
- [Vite](https://vitejs.dev) (for frontend bundling)
- [Quarkus](https://quarkus.io) (for backend development)

### Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/yourusername/MovingStarPlanet.git
    cd MovingStarPlanet
    ```

2. Install dependencies:
    ```bash
    npm install   # For frontend
    ./mvnw compile quarkus:dev   # For backend
    ```

### Running the Simulation

Start the frontend and backend services:
```bash
npm run dev           # Start frontend
./mvnw quarkus:dev    # Start backend
```


## Contributing

### Commit Message Rules

To maintain a consistent commit history, please follow the following commit message format:

- **feat**: Adds a new feature  
- **fix**: Fixes a bug  
- **chore**: Code maintenance (no production code changes)  
- **docs**: Updates or improves documentation  
- **style**: Code style changes (formatting, no logic change)  
- **refactor**: Code restructuring without changing behavior  
- **test**: Adds or updates tests  
- **perf**: Improves performance  
- **ci**: Changes to CI/CD workflows  

### Example:

```bash
git commit -m "feat: add gravitational force calculation"
```

Please note that no code documentation will be provided as it is deemed "useless" by the teacher. Only tests will be performed.
