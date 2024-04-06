import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { DialogClose } from "@radix-ui/react-dialog";
import { Button } from "./ui/button";
import { TypeCryptoLive, TypeCurrency } from "@/lib/api";
import { AssetComponent } from "@/pages/me/Profile";
import { Label } from "@radix-ui/react-dropdown-menu";
import { Input } from "./ui/input";
import { Slider } from "@/components/ui/slider";
import { User } from "@/lib/hooks/useAuth";
import { useEffect, useState } from "react";

interface CreateTradeProps {
  isOpen: boolean;
  onClose: () => void;
  selectedCurrency: string;
  ETH: TypeCryptoLive;
  BTC: TypeCryptoLive;
  possibleCurrencies: TypeCurrency[];
  user: User;
}

export function CreateTrade({
  isOpen,
  onClose,
  ETH,
  BTC,
  selectedCurrency,
  possibleCurrencies,
  user,
}: CreateTradeProps) {
  const [selectedRisk, setSelectedRisk] = useState<number>(user.risk);
  const [selectedAmount, setSelectedAmount] = useState<number>(0);
  const [selectedActualAmount, setSelectedActualAmount] = useState<number>(0);
  const [selectedCrypto, setSelectedCrypto] = useState<string>("BTC");
  const [selectedTakeProfit, setSelectedTakeProfit] = useState<number>(2);
  const [selectedStopLoss, setSelectedStopLoss] = useState<number>(2);
  const [selectedTime, setSelectedTime] = useState<"15m" | "4h" | "1d">("15m");

  useEffect(() => {
    const value = parseFloat(selectedAmount.toString());
    const spread =
      (selectedCrypto === "BTC"
        ? BTC.spread_percentage
        : ETH.spread_percentage) / 100;
    const aux = value * spread;
    const total = value - aux;
    setSelectedActualAmount(+Number(+total).toFixed(2));
  }, [
    BTC.spread_percentage,
    ETH.spread_percentage,
    selectedAmount,
    selectedCrypto,
  ]);

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-3xl min-w-max">
        <DialogHeader>
          <DialogTitle>Create a trade</DialogTitle>
          <DialogDescription>
            This form needs to be filled out to create a trade.
          </DialogDescription>
        </DialogHeader>
        <div className="flex flex-col gap-4">
          <div className="flex flex-row gap-4">
            <div
              className={`rounded-md overflow-hidden flex-1 flex ${
                selectedCrypto === "BTC" ? "border-2 border-primary" : ""
              }`}
              onClick={() => setSelectedCrypto("BTC")}
            >
              <AssetComponent
                crypto={BTC}
                selectedCurrency={selectedCurrency}
                cryptoName="Bitcoin"
                possibleCurrencies={possibleCurrencies}
              />
            </div>
            <div
              className={`rounded-md overflow-hidden flex-1 flex ${
                selectedCrypto === "ETH" ? "border-2 border-primary" : ""
              }`}
              onClick={() => setSelectedCrypto("ETH")}
            >
              <AssetComponent
                crypto={ETH}
                selectedCurrency={selectedCurrency}
                cryptoName="Etherium"
                possibleCurrencies={possibleCurrencies}
              />
            </div>
          </div>
          <div className="flex flex-row gap-4 justify-start items-center">
            <Label className="font-semibold text-base">Amount:</Label>
            <Input
              className="max-w-xs"
              type="number"
              value={selectedAmount}
              onChange={(e) => {
                const value = parseFloat(e.target.value);
                const spread =
                  (selectedCrypto === "BTC"
                    ? BTC.spread_percentage
                    : ETH.spread_percentage) / 100;
                const aux = value * spread;
                const total = value - aux;
                setSelectedAmount(value);
                setSelectedActualAmount(+Number(+total).toFixed(2));
              }}
              placeholder={`Amount in ${selectedCurrency} - ${
                possibleCurrencies.find(
                  (currency) => currency.name === selectedCurrency
                )?.token
              }`}
            />
            <Label className="ml-auto text-base gap-2 w-fit font-normal">
              After taxes:{" "}
              {isNaN(selectedActualAmount) ? 0 : selectedActualAmount}{" "}
              {
                possibleCurrencies.find(
                  (currency) => currency.name === selectedCurrency
                )?.token
              }
            </Label>
          </div>
          <div className="flex flex-row gap-4 justify-center items-center">
            <Label className="font-semibold text-base">Risk:</Label>
            <Slider
              defaultValue={[selectedRisk]}
              max={80}
              step={1}
              min={5}
              onValueChange={(value) => {
                setSelectedRisk(value[0]);
              }}
            />
            <Label className="font-semibold text-base">{selectedRisk}%</Label>
          </div>
          <div className="flex flex-row gap-4 justify-start items-center">
            <Label className="font-semibold text-base">Take profit:</Label>
            <Input
              className="max-w-xs"
              type="number"
              min={2}
              value={selectedTakeProfit}
              onChange={(e) => {
                setSelectedTakeProfit(parseFloat(e.target.value));
              }}
              placeholder="%"
            />
            <Label className="text-base gap-2 w-fit font-normal">%</Label>
          </div>
          <div className="flex flex-row gap-4 justify-start items-center">
            <Label className="font-semibold text-base">Stop loss:</Label>
            <Input
              className="max-w-xs"
              type="number"
              min={2}
              value={selectedStopLoss}
              onChange={(e) => {
                setSelectedStopLoss(parseFloat(e.target.value));
              }}
              placeholder="%"
            />
            <Label className="text-base gap-2 w-fit font-normal">%</Label>
          </div>
          <div className="flex flex-col gap-4 justify-center items-start">
            <Label className="font-semibold text-base">Time:</Label>
            <div className="flex flex-row justify-center items-center w-4/5 self-center">
              <TimeBtn
                time="15m"
                selectedTime={selectedTime}
                setSelectedTime={() => setSelectedTime("15m")}
                inOnTheLeft
                text="Fast trade"
              />
              <TimeBtn
                time="4h"
                selectedTime={selectedTime}
                setSelectedTime={() => setSelectedTime("4h")}
                isOnTheMiddle
                text="Medium trade"
              />
              <TimeBtn
                time="1d"
                selectedTime={selectedTime}
                setSelectedTime={() => setSelectedTime("1d")}
                isOnTheRight
                text="Slow trade"
              />
            </div>
          </div>
        </div>
        <DialogFooter>
          <DialogClose asChild>
            <Button onClick={onClose} type="submit">
              Create Trade
            </Button>
          </DialogClose>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

interface TimeBtnProps {
  time: string;
  selectedTime: string;
  setSelectedTime: (time: string) => void;
  inOnTheLeft?: boolean;
  isOnTheRight?: boolean;
  isOnTheMiddle?: boolean;
  text: string;
}

function TimeBtn({
  time,
  selectedTime,
  setSelectedTime,
  inOnTheLeft,
  isOnTheRight,
  isOnTheMiddle,
  text,
}: TimeBtnProps) {
  return (
    <Button
      className={`flex flex-1 rounded-none ${
        selectedTime === time ? "bg-primary" : "bg-muted"
      } ${inOnTheLeft ? "rounded-l-md" : ""} ${
        isOnTheRight ? "rounded-r-md" : ""
      } ${isOnTheMiddle ? "rounded-none" : ""}`}
      onClick={() => setSelectedTime(time)}
    >
      {text}
    </Button>
  );
}
