import {createContext, useContext, useEffect, useState} from "react";

const SelectedContactContext = createContext();

export const SelectedContactProvider = ({children}) => {

    const resetContact = {
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
        unreadMessages: 0};

    const [selectedContactDetails, setSelectedContactDetails] = useState(resetContact);


    return (
        <SelectedContactContext.Provider value={{selectedContactDetails, setSelectedContactDetails, resetContact}}>
            {children}
        </SelectedContactContext.Provider>
    );
}

export const useSelectedContactContext = () => useContext(SelectedContactContext);