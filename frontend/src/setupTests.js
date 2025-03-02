import '@testing-library/jest-dom';
import { vi } from "vitest";

// Mock ResizeObserver pour JSDOM
global.ResizeObserver = class {
  constructor(callback) {}
  observe() {}
  unobserve() {}
  disconnect() {}
};

// Mock axios pour éviter les erreurs de requête HTTP
vi.mock("axios", () => {
  return {
    default: {
      get: vi.fn(() =>
        Promise.resolve({
          data: [
            { position: [0, 0], velocity: [0, 0], mass: 5, color: "red" },
          ],
        })
      ),
    },
  };
});
