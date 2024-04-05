import Cookies from "js-cookie";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { ModeToggle } from "./mode-toggle";
import { Button } from "./ui/button";

export function Navbar() {
  const [isLogged, setIsLogged] = useState(false);

  useEffect(() => {
    try {
      setIsLogged(true);
    } catch (error) {
      setIsLogged(false);
    }
  }, []);

  return (
    <nav className="flex px-8 py-4 justify-between w-full items-center border-border border-b-2">
      <Link to="/" className="text-primary font-bold text-2xl bg-gradient-to-r">
        planner.ai
      </Link>
      <ul className="flex gap-5 items-center">
        <ModeToggle />
        <li className="text-lg font-semibold">
          <Button>
            <Link to={isLogged ? "/me" : "/auth"}>
              {isLogged ? "Profile" : "Login"}
            </Link>
          </Button>
        </li>
      </ul>
    </nav>
  );
}
