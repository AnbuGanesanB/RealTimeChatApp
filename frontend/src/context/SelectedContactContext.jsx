import {createContext, useContext, useEffect, useState} from "react";

const SelectedContactContext = createContext();

export const SelectedContactProvider = ({children}) => {

    const getDefaultContact = () => ({
        addedDate: null,
        contactPersonOrGroupName: "",
        contactPersonOrGroupId: 0,
        id: 0,
        lastVisitedAt: null,
        nickName: "",
        owner: "",
        ownerId: 0,
        type: "",
        groupMemberDetails: null,
        removedMemberIds:null,
        unreadMessages: 0});

    const resetSelectedContact = () => setSelectedContactDetails(getDefaultContact())

    const [selectedContactDetails, setSelectedContactDetails] = useState(getDefaultContact);


    return (
        <SelectedContactContext.Provider value={{selectedContactDetails, setSelectedContactDetails, resetSelectedContact}}>
            {children}
        </SelectedContactContext.Provider>
    );
}

export const useSelectedContactContext = () => useContext(SelectedContactContext);