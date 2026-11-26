import "../styles/Navbar.css";
import logo from "../assets/logo.svg";
import { useState, useEffect, useRef } from "react";
import { getChampionIcon } from "../services/riotApi";
import { NavLink } from "react-router-dom";

function Navbar({ isLoggedIn = false, onSignOut }) {
    const [profileIcon, setProfileIcon] = useState("");
    const [isProfileOpen, setIsProfileOpen] = useState(false);
    const profileRef = useRef(null);
    const profileButtonRef = useRef(null);

    useEffect(() => {
        getChampionIcon("Ahri")
            .then(setProfileIcon)
            .catch(console.error);
    }, []);

    useEffect(() => {
        if (!isLoggedIn || !isProfileOpen) {
            return;
        }

        function closeWhenClickingOutside(event) {
            if (!profileRef.current.contains(event.target)) {
                setIsProfileOpen(false);
            }
        }

        function closeWithEscape(event) {
            if (event.key === "Escape") {
                setIsProfileOpen(false);
                profileButtonRef.current.focus();
            }
        }

        document.addEventListener("pointerdown", closeWhenClickingOutside);
        document.addEventListener("keydown", closeWithEscape);

        return () => {
            document.removeEventListener("pointerdown", closeWhenClickingOutside);
            document.removeEventListener("keydown", closeWithEscape);
        };
    }, [isLoggedIn, isProfileOpen]);

    function toggleProfile() {
        setIsProfileOpen(!isProfileOpen);
    }

    function closeProfile() {
        setIsProfileOpen(false);
    }

    function signOut() {
        closeProfile();

        if (onSignOut) {
            onSignOut();
        }
    }

    return (
        <header className="navbar">
            <a href="/" className="logo">
                <img src={logo} alt="Summoner Stats" />
            </a>

            <nav className="nav-links">
                <NavLink to="/">Overview</NavLink>
                <NavLink to="/champions">Champions</NavLink>
                <NavLink to="/leaderboards">Leaderboards</NavLink>
                <NavLink to="/community">Community</NavLink>
            </nav>

            {isLoggedIn ? (
                <div className="profile-dropdown" ref={profileRef}>
                    <button
                        className="profile-button"
                        type="button"
                        ref={profileButtonRef}
                        onClick={toggleProfile}
                        aria-expanded={isProfileOpen}
                        aria-controls="profile-dropdown-menu"
                    >
                        {profileIcon && <img src={profileIcon} alt="" />}
                        <span>Profile</span>
                        <span className="profile-arrow" aria-hidden="true"></span>
                    </button>

                    {isProfileOpen && (
                        <nav id="profile-dropdown-menu" className="profile-dropdown-menu" aria-label="Profile">
                            <NavLink to="/profile" onClick={closeProfile}>My Profile</NavLink>
                            <NavLink to="/settings" onClick={closeProfile}>Settings</NavLink>
                            <button type="button" onClick={signOut}>Sign out</button>
                        </nav>
                    )}
                </div>
            ) : (
                <NavLink to="/login" className="profile-button profile-button--login">
                    Login
                </NavLink>
            )}
        </header>
    );
}

export default Navbar;
