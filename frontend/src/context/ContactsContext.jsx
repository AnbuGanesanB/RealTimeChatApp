import {createContext, useContext, useEffect, useState} from "react";
import {useUserContext} from "./UserContext.jsx";
import {BASE_URL} from "../config.js";

const ContactsContext = createContext();

export const ContactsProvider = ({children}) => {

    const token = localStorage.getItem("token");

    const {user} = useUserContext();
    const [contacts, setContacts] = useState([]);
    const resetContacts = () => setContacts([]);

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
            console.log("fetchChatContacts");
            try {
                const response = await fetch(`${BASE_URL}/chatcontacts`, {

                    method: "POST",
                    headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` },
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
       <ContactsContext.Provider value={{contacts, setContacts, updateContactMembers, resetContacts}}>
           {children}
       </ContactsContext.Provider>
    )
}

export const useContactsContext = () => useContext(ContactsContext);