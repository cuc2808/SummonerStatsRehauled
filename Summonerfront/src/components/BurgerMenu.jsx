import "../styles/BurgerMenu.css";
import { useState } from "react";

function BurgerMenu({ title = "Workspace", children }) {
    const [isOpen, setIsOpen] = useState(() => {
        const isMobile = window.matchMedia("(max-width: 640px)").matches;
        return !isMobile;
    });

    function toggleMenu() {
        setIsOpen(!isOpen);
    }

    const menuClass = isOpen ? "burger-menu" : "burger-menu burger-menu--collapsed";
    const toggleLabel = isOpen ? "Fold menu sammen" : "Åbn menu";

    return (
        <aside className={menuClass} aria-label={`${title}-menu`}>
            <div className="burger-menu__heading">
                <span className="burger-menu__title">{title}</span>

                <button
                    className="burger-menu__toggle"
                    type="button"
                    onClick={toggleMenu}
                    aria-expanded={isOpen}
                    aria-controls="workspace-menu"
                    aria-label={toggleLabel}
                    title={toggleLabel}
                >
                    <span className="burger-menu__icon burger-menu__icon--menu" aria-hidden="true" />
                </button>
            </div>

            <div id="workspace-menu" className="burger-menu__items">
                {children}
            </div>
        </aside>
    );
}

export default BurgerMenu;
