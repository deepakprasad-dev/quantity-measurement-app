import { useState } from "react";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import { login, register } from "../api/api";
import { useAuth } from "../context/AuthContext";

const GOOGLE_AUTH_URL = "http://localhost:8080/oauth2/authorization/google";

export default function LoginPage() {
  const [activeTab, setActiveTab] = useState("login");
  const { loginWithToken } = useAuth();
  const navigate = useNavigate();

  // Login state
  const [loginEmail, setLoginEmail] = useState("");
  const [loginPassword, setLoginPassword] = useState("");
  const [loginLoading, setLoginLoading] = useState(false);

  // Register state
  const [regName, setRegName] = useState("");
  const [regEmail, setRegEmail] = useState("");
  const [regPassword, setRegPassword] = useState("");
  const [regLoading, setRegLoading] = useState(false);

  async function handleLogin(e) {
    e.preventDefault();
    setLoginLoading(true);
    try {
      const data = await login(loginEmail, loginPassword);
      loginWithToken(data.token);
      toast.success("Logged in successfully!");
      navigate("/dashboard");
    } catch (err) {
      toast.error(err.message || "Invalid email or password");
    } finally {
      setLoginLoading(false);
    }
  }

  async function handleRegister(e) {
    e.preventDefault();
    setRegLoading(true);
    try {
      await register(regName, regEmail, regPassword);
      toast.success("Account created! Please log in.");
      setActiveTab("login");
      setLoginEmail(regEmail);
    } catch (err) {
      toast.error(err.message || "Registration failed");
    } finally {
      setRegLoading(false);
    }
  }

  return (
    <div className="container">
      <div className="row justify-content-center">
        <div className="col-md-6 col-lg-5">
          <div className="card shadow-sm">
            {/* Tabs */}
            <div className="card-header bg-white">
              <ul className="nav nav-tabs card-header-tabs" role="tablist">
                <li className="nav-item">
                  <button
                    className={`nav-link ${activeTab === "login" ? "active" : ""}`}
                    type="button"
                    onClick={() => setActiveTab("login")}
                  >
                    Login
                  </button>
                </li>
                <li className="nav-item">
                  <button
                    className={`nav-link ${activeTab === "register" ? "active" : ""}`}
                    type="button"
                    onClick={() => setActiveTab("register")}
                  >
                    Register
                  </button>
                </li>
              </ul>
            </div>

            <div className="card-body">
              {activeTab === "login" ? (
                /* Login Form */
                <form onSubmit={handleLogin} id="loginForm">
                  <h5 className="card-title text-center mb-4">Sign In</h5>
                  <div className="mb-3">
                    <label htmlFor="loginEmail" className="form-label">Email</label>
                    <input
                      type="email"
                      className="form-control"
                      id="loginEmail"
                      value={loginEmail}
                      onChange={(e) => setLoginEmail(e.target.value)}
                      required
                    />
                  </div>
                  <div className="mb-3">
                    <label htmlFor="loginPassword" className="form-label">Password</label>
                    <input
                      type="password"
                      className="form-control"
                      id="loginPassword"
                      value={loginPassword}
                      onChange={(e) => setLoginPassword(e.target.value)}
                      required
                    />
                  </div>
                  <button type="submit" className="btn btn-primary w-100 mb-3" disabled={loginLoading}>
                    {loginLoading ? "Signing In..." : "Login"}
                  </button>

                  <div className="text-center text-muted mb-3">OR</div>

                  <a href={GOOGLE_AUTH_URL} className="btn btn-outline-danger w-100">
                    Sign in with Google
                  </a>
                </form>
              ) : (
                /* Register Form */
                <form onSubmit={handleRegister} id="registerForm">
                  <h5 className="card-title text-center mb-4">Create Account</h5>
                  <div className="mb-3">
                    <label htmlFor="registerName" className="form-label">Full Name</label>
                    <input
                      type="text"
                      className="form-control"
                      id="registerName"
                      value={regName}
                      onChange={(e) => setRegName(e.target.value)}
                      required
                    />
                  </div>
                  <div className="mb-3">
                    <label htmlFor="registerEmail" className="form-label">Email</label>
                    <input
                      type="email"
                      className="form-control"
                      id="registerEmail"
                      value={regEmail}
                      onChange={(e) => setRegEmail(e.target.value)}
                      required
                    />
                  </div>
                  <div className="mb-3">
                    <label htmlFor="registerPassword" className="form-label">Password</label>
                    <input
                      type="password"
                      className="form-control"
                      id="registerPassword"
                      value={regPassword}
                      onChange={(e) => setRegPassword(e.target.value)}
                      required
                    />
                  </div>
                  <button type="submit" className="btn btn-primary w-100" disabled={regLoading}>
                    {regLoading ? "Signing Up..." : "Sign Up"}
                  </button>
                </form>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
