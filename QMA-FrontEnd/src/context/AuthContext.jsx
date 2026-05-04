import { createContext, useContext, useState, useEffect, useCallback } from "react";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem("jwt_token"));

  const isAuthenticated = !!token;

  const loginWithToken = useCallback((jwt) => {
    localStorage.setItem("jwt_token", jwt);
    setToken(jwt);
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem("jwt_token");
    setToken(null);
  }, []);

  // Listen for token removal from the api module (session expired)
  useEffect(() => {
    const handler = () => {
      const current = localStorage.getItem("jwt_token");
      if (!current && token) {
        setToken(null);
      }
    };
    window.addEventListener("storage", handler);
    return () => window.removeEventListener("storage", handler);
  }, [token]);

  return (
    <AuthContext.Provider value={{ token, isAuthenticated, loginWithToken, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be inside AuthProvider");
  return ctx;
}
