import {
  AlertDialog,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";

import { Separator } from "@/components/ui/separator";
import { Slider } from "@/components/ui/slider";
import { TypeCryptoLive, TypeCurrency } from "@/lib/api";
import api from "@/lib/api/api";
import { User } from "@/lib/hooks/useAuth";
import { AssetComponent } from "@/pages/me/Profile";
import { DialogClose } from "@radix-ui/react-dialog";
import { Label } from "@radix-ui/react-dropdown-menu";
import { LoaderIcon } from "lucide-react";
import { useContext, useEffect, useState } from "react";
import { Button } from "./ui/button";
import { Input } from "./ui/input";
import { ErrorContext } from "@/lib/contexts/error.context";
import { SuccessContext } from "@/lib/contexts/success.context";
import { AxiosError } from "axios";

interface CreateTradeProps {
  isOpen: boolean;
  onClose: () => void;
  selectedCurrency: string;
  ETH: TypeCryptoLive;
  BTC: TypeCryptoLive;
  possibleCurrencies: TypeCurrency[];
  user: User;
}

export type TradeOpenType = {
  was_already_open: boolean;
  decision: string;
  position_type: string;
  reason: string;
  trade_id: string;
};

export function CreateTrade({
  isOpen,
  onClose,
  ETH,
  BTC,
  selectedCurrency,
  possibleCurrencies,
  user,
}: CreateTradeProps) {
  const { errorMessage, setErrorMessage } = useContext(ErrorContext);
  const { successMessage, setSuccessMessage } = useContext(SuccessContext);

  const [selectedRisk, setSelectedRisk] = useState<number>(user.risk);
  const [selectedAmount, setSelectedAmount] = useState<number>(0);
  const [selectedActualAmount, setSelectedActualAmount] = useState<number>(0);
  const [selectedCrypto, setSelectedCrypto] = useState<string>("BTC");
  const [selectedTakeProfit, setSelectedTakeProfit] = useState<number>(2);
  const [selectedStopLoss, setSelectedStopLoss] = useState<number>(2);
  const [selectedTime, setSelectedTime] = useState<"15m" | "4h" | "1d">("15m");
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const [response, setResponse] = useState<TradeOpenType | null>(null);

  const handleSubmit = async () => {
    setIsLoading(true);
    const body = {
      asset: selectedCrypto,
      spread:
        selectedCrypto === "BTC"
          ? BTC.spread_percentage
          : ETH.spread_percentage,
      risk: selectedRisk,
      assetprice: +(selectedCrypto === "BTC" ? BTC.bid : ETH.bid),
      initialInvestment: selectedActualAmount,
      takeProfit: selectedTakeProfit,
      stopLoss: selectedStopLoss,
      window_money: selectedTime,
    };
    try {
      const res = await api.post("/trade/open", body);
      setResponse(res.data);
      // Handle successful response
    } catch (error: AxiosError | any) {
      if (error.response && error.response.status === 400) {
        // Handle 400 error
        setErrorMessage(error.response.data.error);
      } else {
        // Handle other errors
        console.error("An error occurred:", error.message);
      }
    } finally {
      onClose();
      setIsLoading(false);
    }
  };

  const handleAccept = async () => {
    const res = await api.post(`/trade/activate/${response?.trade_id}`);
    onClose();
    setSuccessMessage("Trade created successfully");
  };

  const handleDecline = async () => {
    const res = await api.post(`/trade/close/${response?.trade_id}`);
    onClose();
    setSuccessMessage("Trade declined successfully");
  };

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
    <>
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
                  text="Fast trade (15-30m)"
                />
                <TimeBtn
                  time="4h"
                  selectedTime={selectedTime}
                  setSelectedTime={() => setSelectedTime("4h")}
                  isOnTheMiddle
                  text="Medium trade (1-9h)"
                />
                <TimeBtn
                  time="1d"
                  selectedTime={selectedTime}
                  setSelectedTime={() => setSelectedTime("1d")}
                  isOnTheRight
                  text="Slow trade (1-3d)"
                />
              </div>
            </div>
          </div>
          <DialogFooter>
            <DialogClose asChild>
              <Button
                onClick={() => {
                  handleSubmit();
                }}
                type="submit"
              >
                Create Trade
              </Button>
            </DialogClose>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      <Dialog open={response !== null} onOpenChange={() => setResponse(null)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Are you absolutely sure?</DialogTitle>
          </DialogHeader>
          <div className="flex flex-col flex-wrap justify-start items-start gap-3">
            <Label className="text-lg font-bold">
              What does the bot want to do?
            </Label>
            <Label className="text-lg font-base">{response?.decision}</Label>
            <Separator />
            {response?.decision !== "DO_NOTHING" ? (
              <>
                <Label className="text-lg font-bold">
                  What type of trade is this?
                </Label>
                <Label className="text-lg font-base">
                  {response?.position_type}
                </Label>
                <Separator />
              </>
            ) : (
              <Label className="text-lg font-base">
                Is recommended to change your parameters in the traded based on
                the bot's decision
              </Label>
            )}
            <Label className="text-lg font-bold">
              What is the reason for this trade?
            </Label>
            <Label className="text-lg font-base">{response?.reason}</Label>
          </div>
          <DialogFooter className="sm:justify-end">
            {response?.decision !== "DO_NOTHING" ? (
              <>
                <DialogClose>
                  <Button
                    onClick={handleDecline}
                    type="button"
                    variant="secondary"
                  >
                    Decline
                  </Button>
                </DialogClose>
                <DialogClose>
                  <Button onClick={handleAccept} type="button">
                    Accept
                  </Button>
                </DialogClose>
              </>
            ) : (
              <DialogClose>
                <Button onClick={handleDecline} type="button">
                  Ok
                </Button>
              </DialogClose>
            )}
          </DialogFooter>
        </DialogContent>
      </Dialog>

      <AlertDialog open={isLoading}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle className="text-center w-full flex-1 flex-col justify-center items-center">
              Loading
            </AlertDialogTitle>
            <AlertDialogDescription className="text-center w-full flex-1 flex-col justify-center items-center">
              <p className="text-center w-full flex-1 flex-col justify-center items-center my-4">
                The bot is thinking about your trade, please be patient
              </p>
              <LoaderIcon className="w-10 h-10 animate-spin mx-auto" />
            </AlertDialogDescription>
          </AlertDialogHeader>
        </AlertDialogContent>
      </AlertDialog>
    </>
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
      } ${isOnTheMiddle ? "rounded-none" : ""}
      
      
        ${
          selectedTime === time
            ? "text-white group:hover:text-white"
            : "text-primary"
        }

        hover:${selectedTime === time ? "text-white" : "text-black"}
      `}
      onClick={() => setSelectedTime(time)}
    >
      <Label
        className={`text-base 
      `}
      >
        {text}
      </Label>
    </Button>
  );
}
