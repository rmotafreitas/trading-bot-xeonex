import {
  AlertDialog,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import { ErrorContext } from "@/lib/contexts/error.context";
import { SuccessContext } from "@/lib/contexts/success.context";
import { useContext } from "react";
import { Button } from "./ui/button";

export function MessageDialog() {
  const { successMessage, setSuccessMessage } = useContext(SuccessContext);
  const { errorMessage, setErrorMessage } = useContext(ErrorContext);

  //   const haruka: ImageSourcePropType =
  //     successMessage !== "" ? HarukaSuccess : HarukaError;

  const message: string = successMessage !== "" ? successMessage : errorMessage;

  const title = successMessage !== "" ? "Success" : "Error";

  const visible: boolean = successMessage !== "" || errorMessage !== "";

  const handleClose = () => {
    setSuccessMessage("");
    setErrorMessage("");
  };

  return (
    <AlertDialog open={visible} onOpenChange={handleClose}>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>{title}</AlertDialogTitle>
          <AlertDialogDescription>{message}</AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <Button onClick={handleClose}>Ok</Button>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}
