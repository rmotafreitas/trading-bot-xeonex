import React, { createContext, useContext, useEffect, useState } from "react";
// @ts-expect-error Because we are using js-cookie
import Cookies from "js-cookie";
import { doAuth, getMe, save as saveAPI } from "../api";
import api from "../api/api";

// Define types for authentication data
type AuthData = {
  user: User | null;
  signIn: (email: string, password: string) => Promise<boolean>;
  signOut: () => void;
  update: (user: User) => Promise<boolean>;
  save: (user: User) => Promise<boolean>;
  addAmount: (amount: number) => Promise<boolean>;
  withdrawAmount: (amount: number) => Promise<boolean>;
};

export type User = {
  login: string;
  role: string;
  balanceInvested: number;
  balanceAvailable: number;
  balanceTotal: number;
  risk: number;
  currency: string;
  lucro: number;
  urlImg: string;
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
    Cookies.remove("token");
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
          console.log(me);
          setUser(me);
          return true;
        }
      }
    }
    Cookies.remove("user");
    setUser(null);
    return false;
  };

  const update = async (user: User): Promise<boolean> => {
    setUser(user);
    return true;
  };

  const save = async (user: User): Promise<boolean> => {
    const res = await saveAPI(user);
    if (res) {
      const updatedUser = await getMe();
      if (updatedUser) {
        setUser(updatedUser);
      }
    }
    return res;
  };

  const addAmount = async (amount: number): Promise<boolean> => {
    if (!user) {
      return false;
    }
    if (amount < 0) {
      return false;
    }

    const newUser = {
      ...user,
      balanceAvailable: user.balanceAvailable + amount,
    };

    const res = await saveAPI(newUser);

    if (res) {
      const updatedUser = await getMe();
      if (updatedUser) {
        setUser(updatedUser);
        return true;
      }
    } else {
      return false;
    }

    return false;
  };

  const withdrawAmount = async (amount: number): Promise<boolean> => {
    if (!user) {
      return false;
    }
    if (amount < 0) {
      return false;
    }

    const newUser = {
      ...user,
      balanceAvailable: user.balanceAvailable - amount,
    };

    try {
      const res = await api.post("/auth/withdraw", { ammount: amount });

      if (res) {
        const updatedUser = await getMe();
        if (updatedUser) {
          setUser(updatedUser);
          return true;
        }
      } else {
        return false;
      }

      return false;
    } catch (error) {
      console.log(error);
      return false;
    }
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
      update,
      save,
      addAmount,
      withdrawAmount,
    }),
    [user]
  );

  return (
    <AuthContext.Provider value={authData}>{children}</AuthContext.Provider>
  );
};

export { useAuth };
