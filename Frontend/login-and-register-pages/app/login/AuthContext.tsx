"use client"

import { createContext, useContext, useState, ReactNode } from "react";

interface AuthContextType {
  username: string;
  role: string;
  isLoggedIn: boolean;
  login: (username: string, password: string) => Promise<boolean>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [username, setUsername] = useState("");
  const [role, setRole] = useState("");
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  const login = async (usernameInput: string, password: string) => {
    try {
      const authHeader = "Basic " + btoa(`${usernameInput}:${password}`);
      const response = await fetch("http://localhost:8080/api/auth/private", {
        method: "GET",
        headers: { Authorization: authHeader },
      });

      if (response.ok) {
        setUsername(usernameInput);
        // For now we assume USER role; can be replaced with a /me endpoint later
        setRole("USER");
        setIsLoggedIn(true);
        return true;
      } else {
        return false;
      }
    } catch (error) {
      console.error(error);
      return false;
    }
  };

  const logout = () => {
    setUsername("");
    setRole("");
    setIsLoggedIn(false);
  };

  return (
    <AuthContext.Provider value={{ username, role, isLoggedIn, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth must be used inside AuthProvider");
  return context;
};
