import { Outlet, useLocation } from "react-router";
import Navbar from "./Navbar";
import BurgerMenu from "./BurgerMenu";

// Tilføj hver sides links, filtre eller andet JSX-indhold under content.
const pageMenus = {
    "/": { title: "Overview", content: null },
    "/champions": { title: "Champions", content: null },
    "/leaderboards": { title: "Leaderboards", content: null },
    "/community": { title: "Community", content: null },
    "/profile": { title: "Profile", content: null },
    "/settings": { title: "Settings", content: null },
    "/login": { title: "Login", content: null },
};

function Layout({ isLoggedIn = false, onSignOut }) {
    const { pathname } = useLocation();
    const currentMenu = pageMenus[pathname] || { title: "Workspace", content: null };

    return (
        <>
            <Navbar
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
