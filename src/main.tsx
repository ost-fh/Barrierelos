import React from "react"
import ReactDOM from "react-dom/client"
import "./i18n";
import App from "./App.tsx"
import {CssBaseline} from "@mui/material";
import {BrowserRouter} from "react-router-dom";


const container = document.getElementById("root")
if (container === null)
  throw new Error("React root container element was not found.")

ReactDOM.createRoot(container).render(
  <React.StrictMode>
    <BrowserRouter>
      <CssBaseline/>
      <App/>
    </BrowserRouter>
  </React.StrictMode>
)
