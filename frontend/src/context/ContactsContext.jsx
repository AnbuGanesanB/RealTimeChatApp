import {createContext, useContext, useEffect, useState} from "react";
import {useUserContext} from "./UserContext.jsx";

const ContactsContext = createContext();

export const ContactsProvider = ({children}) => {

    const {user} = useUserContext();
    const [contacts, setContacts] = useState([]);

    const updateContactMembers = (contactPersonOrGroupId, updatedMemberDetails) => {
        setContacts(prevContacts =>
            prevContacts.map(contact =>
                contact.contactPersonOrGroupId === contactPersonOrGroupId && contact.type === "GROUP"
                    ? { ...contact, groupMemberDetails: updatedMemberDetails }
                    : contact
            )
        );
    };

    useEffect(() => {
        console.log("In Contacts Context.........")
        if (!user?.userId) return; // Ensure user is available before fetching

        const fetchChatContacts = async () => {
            try {
                const response = await fetch("http://localhost:8080/chatcontacts", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ loginUserId: user.userId }),
                });

                if (!response.ok) throw new Error("Request fail!");

                const result = await response.json();
                console.log("Chat Contacts received:", result);
                setContacts(result);
            } catch (error) {
                console.error("Error:", error.message);
            }
        };

        console.log("Fetching chat contacts for user ID:", user.userId);
        fetchChatContacts();
    }, [user.userId]); // Depend on senderId so it re fetches when senderId updates

    return (
       <ContactsContext.Provider value={{contacts, setContacts, updateContactMembers}}>
           {children}
       </ContactsContext.Provider>
    )
}

export const useContactsContext = () => useContext(ContactsContext);