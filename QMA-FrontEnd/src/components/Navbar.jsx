import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";

export default function Navbar() {
  const { isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate("/login");
  }

  return (
    <nav className="navbar navbar-dark bg-gradient-primary py-3 mb-4">
      <div className="container justify-content-center position-relative">
        <span className="navbar-brand mb-0 h3 fw-bold">Welcome To Quantity Measurement</span>
        {isAuthenticated && (
          <button
            className="btn btn-outline-light btn-sm position-absolute end-0"
            onClick={handleLogout}
          >
            Log Out
          </button>
        )}
      </div>
    </nav>
  );
}
