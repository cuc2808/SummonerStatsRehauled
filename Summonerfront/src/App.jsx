import { Routes, Route } from 'react-router-dom';
import './App.css'
import { BrowserRouter } from "react-router-dom";

import Layout from "./components/Layout";
import Overview from "./pages/Overview";
import Champions from "./pages/Champions";
import Leaderboards from "./pages/Leaderboards";
import Community from "./pages/Community";

function App() {

  return (
    <BrowserRouter>

    <Routes>
      <Route element={<Layout />} >
      <Route path="/" element={<Overview />} />
      <Route path="/champions" element={<Champions />} />
      <Route path="/leaderboards" element={<Leaderboards />} />
      <Route path="/community" element={<Community />} />
      </Route>
    </Routes>

    </BrowserRouter>
  )
}

export default App
