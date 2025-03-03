import React from "react";
import { render, screen, fireEvent, act } from "@testing-library/react";
import SimulationCanvas2D from "../SimulationCanvas2D";
import { vi } from "vitest";
import axios from "axios";


//COMMANDE : npm run test:watch


//Mock de l'API pour éviter les appels réseau pendant les tests
// Cela empêche les tests de dépendre d'un backend réel
vi.mock("axios", () => ({
  default: {
    get: vi.fn(() =>
      Promise.resolve({
        data: [{ position: [0, 0], velocity: [0, 0], mass: 5, color: "red" }],
      })
    ),
  },
}));

//Mock du `Canvas` et des composants WebGL pour éviter les erreurs dans l'environnement de test (JSDOM ne supporte pas WebGL)
vi.mock("@react-three/fiber", () => ({
  Canvas: ({ children }) => <div data-testid="canvas">{children}</div>, // Remplace le Canvas par un div factice
  useFrame: vi.fn(), // Mock la fonction useFrame pour éviter les erreurs liées à l'animation
}));

//Mock des composants Three.js pour éviter les erreurs dues à l'absence de support WebGL dans JSDOM
vi.mock("three", () => ({
  Color: class MockColor {
    constructor(color) {
      this.color = color;
    }
  },
  Mesh: () => null,
  Group: () => null,
  MeshBasicMaterial: () => null,
  CircleGeometry: () => null,
}));

//Début de la suite de tests pour le composant SimulationCanvas2D
describe("SimulationCanvas2D", () => {

  //Test 1 : Vérifie que le titre "Simulation N-Body" s'affiche correctement
  test("affiche le titre Simulation N-Body", async () => {
    await act(async () => {
      render(<SimulationCanvas2D />);
    });

    expect(screen.getByText(/Simulation N-Body/i)).toBeInTheDocument();
  });

  //Test 2 : Vérifie que le compteur de particules est bien affiché
  test("affiche le compteur de particules", async () => {
    await act(async () => {
      render(<SimulationCanvas2D />);
    });

    expect(screen.getByText(/NOMBRE DE PARTICULES/i)).toBeInTheDocument();
  });

  //Test 3 : Vérifie que l'ajout de particules avec les boutons fonctionne bien
  test("ajoute des particules avec les boutons +1, +10, +100, +1000", async () => {
    await act(async () => {
      render(<SimulationCanvas2D />);
    });

    const counter = screen.getByText(/NOMBRE DE PARTICULES/i);

    // Fonction utilitaire pour récupérer le nombre de particules affiché
    const getParticleCount = () => {
      const match = counter.textContent.match(/\d+/);
      return match ? parseInt(match[0], 10) : 0;
    };

    let initialCount = getParticleCount();

    fireEvent.click(screen.getByText("+1"));
    expect(getParticleCount()).toBe(initialCount + 1);

    fireEvent.click(screen.getByText("+10"));
    expect(getParticleCount()).toBe(initialCount + 11);

    fireEvent.click(screen.getByText("+100"));
    expect(getParticleCount()).toBe(initialCount + 111);

    fireEvent.click(screen.getByText("+1000"));
    expect(getParticleCount()).toBe(initialCount + 1111);
  });

  //Test 4 : Vérifie qu'on peut ajouter un trou noir et qu'il est comptabilisé
  test("ajoute un trou noir lorsqu'on clique sur 'AJOUTER UN TROU NOIR'", async () => {
    await act(async () => {
      render(<SimulationCanvas2D />);
    });

    const counter = screen.getByText(/NOMBRE DE PARTICULES/i);

    const getParticleCount = () => {
      const match = counter.textContent.match(/\d+/);
      return match ? parseInt(match[0], 10) : 0;
    };

    let initialCount = getParticleCount();

    fireEvent.click(screen.getByText(/AJOUTER UN TROU NOIR/i));

    expect(getParticleCount()).toBe(initialCount + 1);
  });

  //Test 5 : Vérifie que le bouton "PAUSE" fonctionne correctement en alternant entre pause et reprise
  test("pause et reprend la simulation avec le bouton PAUSE", async () => {
    await act(async () => {
      render(<SimulationCanvas2D />);
    });

    const pauseButton = screen.getByText("PAUSE");
    fireEvent.click(pauseButton);
    expect(pauseButton).toHaveTextContent("REPRENDRE");

    fireEvent.click(pauseButton);
    expect(pauseButton).toHaveTextContent("PAUSE");
  });

  //Test 6 : Vérifie que la vitesse de simulation peut être augmentée et diminuée
  test("augmente et diminue la vitesse de la simulation", async () => {
    await act(async () => {
      render(<SimulationCanvas2D />);
    });

    const speedLabel = screen.getByText(/VITESSE : 0.10/i);

    const increaseSpeedButton = screen.getByText("+");
    fireEvent.click(increaseSpeedButton);
    expect(screen.getByText(/VITESSE : 0.15/i)).toBeInTheDocument();

    const decreaseSpeedButton = screen.getByText("-");
    fireEvent.click(decreaseSpeedButton);
    expect(screen.getByText(/VITESSE : 0.10/i)).toBeInTheDocument();
  });

  //Test 7 : Vérifie que la description du projet peut être affichée et masquée
  test("affiche et masque la description du projet", async () => {
    await act(async () => {
      render(<SimulationCanvas2D />);
    });

    const toggleDescriptionButton = screen.getByText("DESCRIPTION DU PROJET");
    fireEvent.click(toggleDescriptionButton);
    expect(screen.getByText(/Ce projet est une simulation interactive N-Body en 2D/i)).toBeInTheDocument();

    fireEvent.click(toggleDescriptionButton);
    expect(screen.queryByText(/Ce projet est une simulation interactive N-Body en 2D/i)).not.toBeInTheDocument();
  });

  //Test 8 : Vérifie que le bouton "NETTOYER" réinitialise bien le nombre de particules à 0
  test("nettoie le canvas lorsqu'on clique sur le bouton 'NETTOYER'", async () => {
    await act(async () => {
      render(<SimulationCanvas2D />);
    });

    const counter = screen.getByText(/NOMBRE DE PARTICULES/i);

    // Fonction utilitaire pour récupérer le nombre de particules affiché
    const getParticleCount = () => {
      const match = counter.textContent.match(/\d+/);
      return match ? parseInt(match[0], 10) : 0;
    };

    let initialCount = getParticleCount();

    // Ajout de 10 particules avant nettoyage
    fireEvent.click(screen.getByText("+10"));
    expect(getParticleCount()).toBe(initialCount + 10);

    // Nettoyage du canvas
    fireEvent.click(screen.getByText("NETTOYER"));

    // Vérification que le compteur revient à 0
    expect(getParticleCount()).toBe(0);
  });
});
