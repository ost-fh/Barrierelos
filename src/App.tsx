import "./App.css"
import {Route, Routes} from "react-router-dom";
import WebsitePage from "./pages/website/WebsitePage.tsx";
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
        <Route path="/websites/:websiteId" element={<WebsitePage/>}/>
        <Route path="*" element={<NotFound/>}/>
      </Routes>
    </>
  )
}

export default App
