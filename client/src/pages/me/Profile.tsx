import { Navbar } from "@/components/navbar";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/lib/hooks/useAuth";
import { useNavigate } from "react-router-dom";

export function ProfilePage() {
  const { user, signOut } = useAuth();

  const router = useNavigate();

  const handleLogout = async () => {
    await signOut();
    router("/auth");
  };

  return (
    <div className="flex flex-col min-h-screen">
      <Navbar />
      <section className="flex-1 flex flex-col justify-center items-center">
        <div className="flex flex-col gap-4 max-w-xl w-full p-8 border-border border-2 rounded-lg max-sm:border-0">
          <h1 className="font-black text-3xl justify-center items-center text-center flex flex-row gap-4">
            <p>My Profile</p>
          </h1>
          <Button
            onClick={handleLogout}
            className="flex flex-1 max-sm:w-full justify-center items-center"
          >
            Logout
          </Button>
        </div>
      </section>
    </div>
  );
}
