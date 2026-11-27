import { Routes, Route } from 'react-router-dom';
import './App.css'
import { BrowserRouter } from "react-router-dom";

import Layout from "./components/Layout";
import Overview from "./pages/Overview";
import Champions from "./pages/Champions";
import Leaderboards from "./pages/Leaderboards";
import Community from "./pages/Community";
import Profile from "./pages/Profile";
import Settings from "./pages/Settings";
import Login from "./pages/Login";
import Register from "./pages/Register";

function App() {

  return (
    <BrowserRouter>

    <Routes>
      <Route element={<Layout />} >
      <Route path="/" element={<Overview />} />
      <Route path="/champions" element={<Champions />} />
      <Route path="/leaderboards" element={<Leaderboards />} />
      <Route path="/community" element={<Community />} />
      <Route path="/profile" element={<Profile />} />
      <Route path="/settings" element={<Settings />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      </Route>
    </Routes>

    </BrowserRouter>
  )
}

export default App
