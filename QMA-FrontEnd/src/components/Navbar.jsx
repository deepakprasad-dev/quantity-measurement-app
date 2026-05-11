import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";

export default function Navbar() {
  const { isAuthenticated, logout, userEmail } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate("/login");
  }

  return (
    <nav className="navbar navbar-dark bg-gradient-primary py-3 mb-4">
      <div className="container-fluid justify-content-center position-relative">
        {isAuthenticated && (
          <div className="position-absolute start-0 ps-4 d-flex align-items-center">
            <span className="text-light fw-medium fs-5">
              Hello, {userEmail ? userEmail.split('@')[0] : 'User'}!
            </span>
          </div>
        )}
        <span className="navbar-brand mb-0 h3 fw-bold">Welcome To Quantity Measurement</span>
        {isAuthenticated && (
          <div className="position-absolute end-0 pe-4 d-flex align-items-center">
            <button
              className="btn btn-outline-light btn-sm"
              onClick={handleLogout}
            >
              Log Out
            </button>
          </div>
        )}
      </div>
    </nav>
  );
}
