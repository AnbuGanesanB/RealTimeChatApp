import {useState, useContext, createContext} from "react";

const RecipientContext = createContext();

export const RecipientProvider = ({children}) => {

    const [recipientId, setRecipientId] = useState(0);
    const [previousContactId, setPreviousContactId] = useState(0);


    return (
        <RecipientContext.Provider value={{recipientId, setRecipientId, previousContactId, setPreviousContactId}}>
            {children}
        </RecipientContext.Provider>
    );
}

export const useRecipientContext = () => useContext(RecipientContext);