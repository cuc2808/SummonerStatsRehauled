import { NavLink } from "react-router-dom";

function Login() {
    return (
        <main>
            <h1>Login</h1>
            <p>Username</p>
            <input type input name="username" placeholder="Username..."/>
            <p>Password</p>
            <input type="password"
            name="password" placeholder="Password..."/>
            <NavLink path="/register">New User?</NavLink>
        </main>
    );
}

export default Login;
