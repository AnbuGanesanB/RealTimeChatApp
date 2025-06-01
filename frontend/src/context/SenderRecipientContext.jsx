import {useState, useContext, createContext, useEffect} from "react";

const SenderRecipientContext = createContext();

export const useSenderRecipient = () => useContext(SenderRecipientContext);

export const SenderRecipientProvider = ({children}) => {
    const [senderRecipient, setSenderRecipient] = useState({
        senderId: "100",
        recipientId: null,
        message: null
    });

    useEffect(() => {
        console.log("Updated State (Post Render):", senderRecipient.recipientId);
        console.log(senderRecipient);
    },[senderRecipient]);

    return (
        <SenderRecipientContext.Provider value={{senderRecipient, setSenderRecipient}}>
            {children}
        </SenderRecipientContext.Provider>
    );
}