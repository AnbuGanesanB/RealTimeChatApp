import {useRecipientContext} from "../context/RecipientContext.jsx";
import {useEffect} from "react";
import {useSelectedContactContext} from "../context/SelectedContactContext.jsx";
import {useContactsContext} from "../context/ContactsContext.jsx";
import {useStompClientContext} from "../context/StompClientContext.jsx";

export function useSideEffects() {

    const { recipientId } = useRecipientContext();
    const {receivedMessage, newReceivedContact, updatedContact} = useStompClientContext();
    const {selectedContactDetails, setSelectedContactDetails} = useSelectedContactContext();
    const {contacts, setContacts} = useContactsContext();


    //--Whenever Recipient is selected, Unread count set to zero
    useEffect(() => {
        if (!recipientId) return; // Ensure recipientId is selected

        setContacts(prevContacts =>
            prevContacts.map(contact =>
                contact.id === recipientId ? { ...contact, unreadMessages: 0 } : contact
            )
        );
    }, [recipientId]);

    //--Updated Contact is Received and thereby updating existing List.
    //--Meanwhile, if Updated contact is selected one - Unread always be zero;
    useEffect(() => {
        if (!updatedContact) return;

        setContacts(prevContacts => {
            const updatedList = prevContacts.map(contact => {
                if (contact.id === updatedContact.id) {
                    const adjustedContact = (updatedContact.id === selectedContactDetails.id)
                        ? { ...updatedContact, unreadMessages: 0 }
                        : updatedContact;
                    return adjustedContact;
                }
                return contact;
            });
            console.log("Updated list:", updatedList);
            return updatedList;
        });
    }, [updatedContact, selectedContactDetails]);


    /**
    * If Updated contact is also the selected contact -- Update the selected contact also
    */
    useEffect(() => {
        if (!updatedContact) return;

        if(selectedContactDetails.id === updatedContact.id) {
            const updatedContactDetails = {
                addedDate: updatedContact.addedDate,
                contactPersonOrGroupName: updatedContact.contactPersonOrGroupName,
                contactPersonOrGroupId: updatedContact.contactPersonOrGroupId,
                id: updatedContact.id,
                lastVisitedAt: updatedContact.lastVisitedAt,
                nickName: updatedContact.nickName,
                owner: updatedContact.owner,
                ownerId: updatedContact.ownerId,
                type: updatedContact.type,
                groupMemberDetails: updatedContact.groupMemberDetails,
                aboutMe: updatedContact.aboutMe,
                initials: updatedContact.initials,
                dpPath: updatedContact.dpPath,
                dpAvailable: updatedContact.dpAvailable,
                onlineStatus: updatedContact.onlineStatus,
            };

            console.log("Setting updated contact details for Selected contact:", updatedContactDetails);
            setSelectedContactDetails(updatedContactDetails);
        }

    }, [updatedContact]);


    /**
     * New Contact Received - appended to existing contacts
     */
    useEffect(() => {
        if (newReceivedContact && newReceivedContact.id) {
            setContacts((prevContacts) => {
                const updated = [...prevContacts, newReceivedContact];
                console.log("Updated Contacts:");
                updated.forEach(contact => console.log(contact));
                return updated;
            });
        }
    }, [newReceivedContact]);

    /*useEffect(()=>{
        if (!updatedContact) return;
        setContacts(prevContacts => {
            const updatedList = prevContacts.map(contact => (contact.id === updatedContact.id ? updatedContact : contact));
            console.log("Updated list:", updatedList);
            return updatedList;
        });

    },[updatedContact])*/

}