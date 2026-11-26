import { Routes, Route } from 'react-router-dom';
import './App.css'
import Navbar from './components/Navbar';

import Overview from "./pages/Overview";
import Champions from "./pages/Champions";
import Leaderboards from "./pages/Leaderboards";
import Community from "./pages/Community";

function App() {

  return (

    
    <>
    <Navbar />

    <Routes>
      <Route path="/" element={<Overview />} />
      <Route path="/champions" element={<Champions />} />
      <Route path="/leaderboards" element={<Leaderboards />} />
      <Route path="/community" element={<Community />} />
    </Routes>
    </>
  )
}

export default App
