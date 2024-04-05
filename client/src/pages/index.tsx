import { Navbar } from "@/components/navbar";
import {
  Card,
  CardBody,
  CardHeader,
  Typography,
} from "@material-tailwind/react";
import Chart from "react-apexcharts";

export function HomePage() {
  return (
    <div className="flex flex-col gap-6 min-h-screen min-w-full">
      <Navbar />
      <div className="w-full bg-inherit bg-no-repeat bg-cover min-h-96 justify-center items-center flex flex-col">
        <div>
          <h1>
            Win money by doing absolutely nothing! Just click the button below
            to start trading. <span>Be millionaire in no time!</span>
          </h1>
          <h2>
            Simply sign up and start trading to earn money. It's that simple!
          </h2>
        </div>
        <aside>
          <img src="" alt="" />
        </aside>
      </div>
    </div>
  );
}
