// @ts-expect-error Js cookies is not typed
import Cookies from "js-cookie";

import { Navbar } from "@/components/navbar";
import { hankoApi, hankoInstance } from "@/lib/hanko";
import { register } from "@teamhanko/hanko-elements";
import { useEffect, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "@/lib/hooks/useAuth";

export function LoginPage() {
  const router = useNavigate();

  const { signIn } = useAuth();

  const hanko = useMemo(() => hankoInstance, []);

  const handleAuthFlowCompleted = async () => {
    const hankoUser = await hanko.user.getCurrent();
    const hankoCookie = await Cookies.get("hanko");
    if (hankoUser?.email && hankoCookie) {
      console.log(hankoUser);
      console.log(hankoCookie);
      signIn(hankoUser.email, hankoCookie);
      router("/me");
    }
  };

  useEffect(
    () =>
      hanko.onAuthFlowCompleted(() => {
        handleAuthFlowCompleted();
      }),
    [hanko]
  );

  useEffect(() => {
    register(hankoApi).catch((error) => {
      console.error(error);
      // handle error
    });
  }, []);

  return (
    <div className="flex flex-col min-h-screen">
      <Navbar />
      <section className="flex flex-1 flex-col justify-center gap-20 items-center">
        <h1 className="font-bold text-5xl text-center">Start earning money!</h1>
        <hanko-auth />
      </section>
    </div>
  );
}
