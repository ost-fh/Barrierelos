import {NavLink} from "react-router-dom"
import "./NavBar.css"

function NavBar() {
  return (
    <>
      <nav>
        <ul>
          <li>
            <NavLink to="/">Home</NavLink>
          </li>
          <li>
            <NavLink to="/website/1">Website</NavLink>
          </li>
        </ul>
      </nav>
    </>
  )
}

export default NavBar
