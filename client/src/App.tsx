import { Route, BrowserRouter as Router, Routes } from "react-router-dom";

import { HomePage } from "./pages";
import { LoginPage } from "./pages/Login";
import { ProfilePage } from "./pages/me/Profile";
import { ThemeProvider } from "./components/theme-provider";
import { NotFoundPage } from "./pages/NotFound";
import { AuthProvider } from "./lib/hooks/useAuth";
import { useMemo, useState } from "react";
import { ErrorContext } from "./lib/contexts/error.context";
import { SuccessContext } from "./lib/contexts/success.context";
import { MessageDialog } from "./components/message-dialog";
import { TradePage } from "./pages/me/trade";

export type TradeParams = {
  tradeId: string;
};

export function App() {
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const errorMessageProvider = useMemo(
    () => ({ errorMessage, setErrorMessage }),
    [errorMessage, setErrorMessage]
  );

  const successMessageProvider = useMemo(
    () => ({ successMessage, setSuccessMessage }),
    [successMessage, setSuccessMessage]
  );

  return (
    <SuccessContext.Provider value={successMessageProvider}>
      <ErrorContext.Provider value={errorMessageProvider}>
        <AuthProvider>
          <ThemeProvider defaultTheme="white" storageKey="vite-ui-theme">
            <Router>
              <Routes>
                <Route path="/" element={<HomePage />} />
                <Route path="/auth" element={<LoginPage />} />
                <Route path="/me" element={<ProfilePage />} />
                <Route path="/me/trade/:tradeId" element={<TradePage />} />
                <Route path="*" element={<NotFoundPage />} />
              </Routes>
            </Router>
          </ThemeProvider>
        </AuthProvider>
        <MessageDialog />
      </ErrorContext.Provider>
    </SuccessContext.Provider>
  );
}
