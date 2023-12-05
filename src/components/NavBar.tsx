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
            <NavLink to="/websites/1">Website 1</NavLink>
          </li>
          <li>
            <NavLink to="/websites/2">Website 2</NavLink>
          </li>
        </ul>
      </nav>
    </>
  )
}

export default NavBar
