import "../styles/Navbar.css";
import logo from "../assets/logo.svg";
import { useState, useEffect } from "react";
import { getChampionIcon } from "../services/riotApi";

function Navbar() {

    const [profileIcon, setProfileIcon] = useState("");

    useEffect(() => {
        getChampionIcon("Ahri")
            .then(setProfileIcon)
            .catch(console.error);
}, []);

    return (
        <header className="navbar">
            <a href="/" className="logo">
                <img src={logo} alt="Summoner Stats" />
            </a>
            <nav className="nav-links">
            <a href="/" className="active">Overview</a>
            <a href="/champions">Champions</a>
            <a href="/leaderboards">Leaderboards</a>
            <a href="/community">Community</a>
            </nav>

            <button className="profile-button">
                {profileIcon && <img src={profileIcon} alt="Ahri" />}

                <span>Profile</span>
                <span className="profile-arrow"></span>
            </button>
        </header>
    );
}
export default Navbar;