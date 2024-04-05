import React, { createContext, useContext, useEffect, useState } from "react";
// @ts-expect-error Because we are using js-cookie
import Cookies from "js-cookie";
import { doAuth, getMe } from "../api";

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

// Create context for authentication data
const AuthContext = createContext<AuthData | undefined>(undefined);

// Custom hook to use authentication context
const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};

// AuthProvider component to wrap your app and provide authentication context
export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}: {
  children: React.ReactNode;
}) => {
  const [user, setUser] = useState<User | null>(null);

  const signIn = async (login: string, password: string): Promise<boolean> => {
    const res = await doAuth(login, password);
    if (res) {
      const user = await getMe();
      if (user) {
        setUser(user);
        Cookies.set("user", JSON.stringify({ login, password }));
      } else {
        return false;
      }
    }
    return res;
  };

  const signOut = () => {
    Cookies.remove("user");
    Cookies.remove("hanko");
    setUser(null);
  };

  const fetchUserFromCookies = async (): Promise<boolean> => {
    const storedUser = Cookies.get("user");
    if (storedUser) {
      const { login, password } = JSON.parse(storedUser);
      const res = await doAuth(login, password);
      if (res) {
        const me = await getMe();
        if (me) {
          setUser(me);
          return true;
        }
      }
    }
    Cookies.remove("user");
    setUser(null);
    return false;
  };

  useEffect(() => {
    console.log("Fetching user from cookies");
    fetchUserFromCookies();
  }, []);

  // Memoize value to prevent unnecessary re-renders
  const authData = React.useMemo(
    () => ({
      user,
      signIn,
      signOut,
    }),
    [user]
  );

  return (
    <AuthContext.Provider value={authData}>{children}</AuthContext.Provider>
  );
};

export { useAuth };
