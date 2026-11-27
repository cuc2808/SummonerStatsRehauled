import { Outlet, useLocation, NavLink } from "react-router";
import Navbar from "./Navbar";
import BurgerMenu from "./BurgerMenu";

const pageMenus = {
    "/": { title: "Overview", content: 
      (
        <nav className="burger-menu__items">
            <NavLink
                to="/"
                end
                className={({ isActive }) =>
                    isActive
                        ? "burger-menu__item burger-menu__item--active"
                        : "burger-menu__item"
                }
            >
                <span
                    className="burger-menu__icon burger-menu__icon--house"
                    aria-hidden="true"
                />
                <span className="burger-menu__label">Home</span>
            </NavLink>
            <NavLink
                to="/search-account"
                end
                className={({ isActive }) =>
                    isActive
                        ? "burger-menu__item burger-menu__item--active"
                        : "burger-menu__item"
                }
            >
                <span
                    className="burger-menu__icon burger-menu__icon--search"
                    aria-hidden="true"
                />
                <span className="burger-menu__label">Account Search</span>
            </NavLink>
        </nav>
      )
     },
    "/champions": { title: "Champions", content: null },
    "/leaderboards": { title: "Leaderboards", content: null },
    "/community": { title: "Community", content: null },
    "/profile": { title: "Profile", content: null },
    "/settings": { title: "Settings", content: null },
    "/login": { title: "Login", content: null },
};

// Undersider kan bruge menuen fra deres overordnede sektion.
const menuSections = {
    "/search-account": "/",
};

function Layout({ isLoggedIn = false, onSignOut }) {
    const { pathname } = useLocation();
    const menuPath = menuSections[pathname] || pathname;
    const currentMenu = pageMenus[menuPath] || { title: "Workspace", content: null };

    return (
        <>
            <Navbar
                activeSection={menuPath}
                isLoggedIn={isLoggedIn}
                onSignOut={onSignOut}
            />

            <div className="workspace-layout">
                <BurgerMenu title={currentMenu.title}>
                    {currentMenu.content}
                </BurgerMenu>

                <div className="workspace-content">
                    <Outlet />
                </div>
            </div>

            <footer>This is a footer</footer>
        </>
    );
}

export default Layout;