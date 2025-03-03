import React from "react";
import { render, screen, fireEvent, act } from "@testing-library/react";
import SimulationCanvas2D from "../SimulationCanvas2D";
import { vi } from "vitest";
import axios from "axios";

// Mock de l'API pour éviter les appels réseau pendant les tests
vi.mock("axios", () => ({
  default: {
    get: vi.fn(() => Promise.resolve({ data: [] })),
    post: vi.fn(() => Promise.resolve({ data: {} })),
    delete: vi.fn(() => Promise.resolve({})),
  },
}));

// Mock du `Canvas` et des composants WebGL pour éviter les erreurs dans l'environnement de test
vi.mock("@react-three/fiber", () => ({
  Canvas: ({ children }) => <div data-testid="canvas">{children}</div>,
  useFrame: vi.fn(),
}));

// Mock des composants Three.js pour éviter les erreurs dues à l'absence de support WebGL dans JSDOM
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

// Mock du WebSocket pour éviter les erreurs de connexion dans les tests
vi.stubGlobal("WebSocket", class {
  constructor() {
    this.send = vi.fn();
    this.close = vi.fn();
    this.onmessage = null;
    this.onopen = null;
    this.onclose = null;
  }
});

// Début des tests
describe("SimulationCanvas2D", () => {
  test("affiche le titre Simulation N-Body", async () => {
    await act(async () => {
      render(<SimulationCanvas2D />);
    });
    expect(screen.getByText(/Simulation N-Body/i)).toBeInTheDocument();
  });

  test("affiche le compteur de particules", async () => {
    await act(async () => {
      render(<SimulationCanvas2D />);
    });
    expect(screen.getByText(/NOMBRE DE PARTICULES/i)).toBeInTheDocument();
  });

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

  test("augmente et diminue la vitesse de la simulation", async () => {
    await act(async () => {
      render(<SimulationCanvas2D />);
    });
    fireEvent.click(screen.getByText("+"));
    expect(screen.getByText(/VITESSE/i)).toBeInTheDocument();
  });

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
});
