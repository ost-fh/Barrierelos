import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.tsx'
import './index.css'
import {CssBaseline} from "@mui/material";

const container = document.getElementById('root')
if (container === null)
  throw new Error('React root container element was not found.')

ReactDOM.createRoot(container).render(
  <React.StrictMode>
    <CssBaseline/>
    <App/>
  </React.StrictMode>,
)
