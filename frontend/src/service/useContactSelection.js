import { useRecipientContext } from "../context/RecipientContext.jsx";
import { useSelectedContactContext } from "../context/SelectedContactContext.jsx";

export function useContactSelection() {
    const {recipientId, setRecipientId, setPreviousContactId} = useRecipientContext();
    const { setSelectedContactDetails } = useSelectedContactContext();

    const handleSelection = (contact) => {
        console.log("In handle selection Method");
        if (contact.id === recipientId) return;
        console.log("contact.id !== recipientId");
        console.log("Before assigning values...");
        console.log("Actual current contact.id is: ", contact.id);
        console.log("Actual current recipientId is: " ,recipientId);

        setPreviousContactId(recipientId);
        setRecipientId(contact.id);

        console.log("After assigning values...");
        console.log("Actual current contact.id is: ", contact.id);
        console.log("Actual current recipientId is: " ,recipientId);

        const newContactDetails = {
            addedDate: contact.addedDate,
            contactPersonOrGroupName: contact.contactPersonOrGroupName,
            contactPersonOrGroupId: contact.contactPersonOrGroupId,
            id: contact.id,
            lastVisitedAt: contact.lastVisitedAt,
            nickName: contact.nickName,
            owner: contact.owner,
            ownerId: contact.ownerId,
            type: contact.type,
            groupMemberDetails: contact.groupMemberDetails,
            removedMemberIds: contact.removedMemberIds,
            aboutMe: contact.aboutMe,
            initials: contact.initials,
            dpPath: contact.dpPath,
            dpAvailable: contact.dpAvailable,
            onlineStatus: contact.onlineStatus,
            lastMessageFromUser: contact.lastMessageFromUser,
            lastMessageContent: contact.lastMessageContent,
            lastMessageSenderId: contact.lastMessageSenderId,
        };

        console.log("Setting contact details:", newContactDetails);
        setSelectedContactDetails(newContactDetails);
    };

    return handleSelection;
}


