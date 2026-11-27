import "../styles/Register.css";
import { NavLink } from "react-router-dom";

function Register() {
    return (
        <section className="register-page">
            <div className="register-card">

                <h1>Register</h1>

                <p>
                    Please fill out the form below to create an account.
                </p>

                <form className="register-form">
                    <div className="form-group">
                        <label htmlFor="username">Username</label>
                        <input
                            type="text"
                            id="username"
                            name="username"
                            placeholder="Enter your username"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="email">Email</label>
                        <input
                            type="email"
                            id="email"
                            name="email"
                            placeholder="Enter your email"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="password">Password</label>
                        <input
                            type="password"
                            id="password"
                            name="password"
                            placeholder="Enter your password"
                            required
                        />
                    </div>

                    <button type="submit" className="register-button">
                        Register
                    </button>
                </form>
                <div className="register-login">
                    <NavLink to="/login">
                        Already a user? Sign in.
                    </NavLink>
                </div>
            </div>
        </section>
    );
}

export default Register;