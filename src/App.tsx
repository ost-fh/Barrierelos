import "./App.css"
import {Route, Routes} from "react-router-dom";
import Website from "./pages/Website.tsx";
import Home from "./pages/Home.tsx";
import {NotFound} from "./pages/NotFound.tsx";
import NavBar from "./components/NavBar.tsx";

function App() {
  return (
    <>
      <header>
        <NavBar/>
      </header>
      <Routes>
        <Route path="/" element={<Home/>}/>
        <Route path="/website/:websiteId" element={<Website/>}/>
        <Route path="*" element={<NotFound/>}/>
      </Routes>
    </>
  )
}

export default App
