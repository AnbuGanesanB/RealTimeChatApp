import { useRecipientContext } from "../context/RecipientContext.jsx";
import { useSelectedContactContext } from "../context/SelectedContactContext.jsx";

export function useContactSelection() {
    const {recipientId, setRecipientId, setPreviousContactId} = useRecipientContext();
    const { setSelectedContactDetails } = useSelectedContactContext();

    const handleSelection = (contact) => {
        console.log("In handle selection Method");
        if (contact.id === recipientId) return;
        console.log("contact.id !== recipientId");
        console.log("Actual current recipientId is " + recipientId);
        setPreviousContactId(prev => recipientId);
        setRecipientId(contact.id);

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
            aboutMe: contact.aboutMe,
            initials: contact.initials,
            dpPath: contact.dpPath,
            dpAvailable: contact.dpAvailable,
            onlineStatus: contact.onlineStatus,
        };

        console.log("Setting contact details:", newContactDetails);
        setSelectedContactDetails(newContactDetails);
    };

    return handleSelection;
}


