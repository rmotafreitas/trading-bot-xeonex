import { useContext, useEffect, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import { Navbar } from "@/components/navbar";

export function LoginPage() {
  const router = useNavigate();

  useEffect(() => []);

  return (
    <div className="flex flex-col min-h-screen">
      <Navbar />
      <section className="flex flex-1 flex-col justify-center gap-20 items-center">
        <h1 className="font-bold text-5xl text-center">Start working faster</h1>
      </section>
    </div>
  );
}
