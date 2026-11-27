import { NavLink } from "react-router-dom";

function Login() {
    return (
        <main>
            <form>
            <h1>Login</h1>
            <p>Username</p>
            <input type input name="username" placeholder="Username..."/>
            <p>Password</p>
            <input type="password"
            name="password" placeholder="Password..."/>
            <button type="submit">Login</button>
            <NavLink to="/register">New User?</NavLink>
            </form>
        </main>
    );
}

export default Login;
