import React from "react";
import { render, screen, act } from "@testing-library/react";
import App from "../../App";

test("affiche le titre Simulation N-Body", async () => {
  await act(async () => {
    render(<App />);
  });

  expect(screen.getByText(/Simulation N-Body/i)).toBeInTheDocument();
});
