import {useState, useContext, createContext} from "react";

const SenderContext = createContext();

export const useSenderContext = () => useContext(SenderContext);

export const SenderProvider = ({children}) => {

    const [senderId, setSenderId] = useState(0);

    return (
        <SenderContext.Provider value={{senderId, setSenderId}}>
            {children}
        </SenderContext.Provider>
    );
}