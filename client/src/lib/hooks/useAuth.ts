import { useState, useEffect } from "react";
// @ts-expect-error Because we are using js-cookie
import Cookies from "js-cookie";
import {
  createUser as createUserFunction,
  login as loginFunction,
} from "../api";

// Define types for authentication data
type AuthData = {
  user: User | null;
  signIn: (email: string, password: string) => void;
  signOut: () => void;
};

type User = {
  user: string;
  login: string;
  // Add more user-related fields as needed
};

// Your custom hook
const useAuth = (): AuthData => {
  // State to hold the authenticated user
  const [user, setUser] = useState<User | null>(null);

  // Mock signIn function
  const signIn = async (user: string, login: string) => {
    await loginFunction(user, login);
    setUser({ user, login });
  };

  const signOut = () => {
    Cookies.remove("userjwt");
    setUser(null);
  };

  useEffect(() => {
    async function fetchUser(user: string, login: string): Promise<boolean> {
      const storedUser = localStorage.getItem("user");
      if (storedUser) {
        return await loginFunction(user, login);
      }
      return false;
    }

    const storedJwtUser = Cookies.get("userjwt");
    const storedUser = localStorage.getItem("user");
    if (storedUser && storedJwtUser) {
      if (await fetchUser(storedUser, storedJwtUser)) {
        setUser(JSON.parse(storedUser));
      } else {
        Cookies.remove("userjwt");
        localStorage.removeItem("user");
      }
    }
  }, []);

  // Effect to update localStorage when user changes
  useEffect(() => {
    if (user) {
      localStorage.setItem("user", JSON.stringify(user));
    } else {
      localStorage.removeItem("user");
    }
  }, [user]);

  // Return the authentication data and functions
  return { user, signIn, signOut };
};

export default useAuth;
