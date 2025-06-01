import ChatPage from "./pages/ChatPage.jsx";
import {SenderRecipientProvider} from "./context/SenderRecipientContext.jsx";
import {SenderProvider} from "./context/SenderContext.jsx";
import {RecipientProvider} from "./context/RecipientContext.jsx";
import {UserProvider} from "./context/UserContext.jsx";
import {SelectedContactProvider} from "./context/SelectedContactContext.jsx";
import {ContactsProvider} from "./context/ContactsContext.jsx";
import { Routes, Route } from "react-router-dom";
import LoginPage from "./pages/LoginPage.jsx";
import {StompClientProvider} from "./context/StompClientContext.jsx";

function App() {

  return (
      <UserProvider>
          <SenderProvider>
              <ContactsProvider>
                  <SelectedContactProvider>
                      <RecipientProvider>
                          <StompClientProvider>
                              <main className="main-content">
                                  <Routes>
                                      <Route path="/login" element={<LoginPage />} />
                                      <Route path="/home" element={<ChatPage />} />
                                  </Routes>
                              </main>
                          </StompClientProvider>
                      </RecipientProvider>
                  </SelectedContactProvider>
              </ContactsProvider>
          </SenderProvider>
      </UserProvider>
  )
}

export default App
