import { Outlet } from "react-router";
import Navbar from "./Navbar";

export default function Layout() {
  return (
    <> 
    <Navbar />

      <main>
        <Outlet />
      </main>

      <footer>This is a footer</footer>
    </>
  );
}