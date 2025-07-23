import {useEffect} from "react";
import {Modal} from "bootstrap";

export const useBootstrapModalClose = (modalId, onClose) => {
    useEffect(() => {
        const modalEl = document.getElementById(modalId);
        if (!modalEl) return;

        const modal = Modal.getOrCreateInstance(modalEl);
        modal.show();

        modalEl.addEventListener('hidden.bs.modal', onClose);

        return () => {
            modalEl.removeEventListener('hidden.bs.modal', onClose);
            modal.dispose();
        };
    }, []);
};

export const modalHide = (modalId) => {
    const modalEl = document.getElementById(modalId);
    const modalInstance = Modal.getOrCreateInstance(modalEl);
    modalInstance.hide();
}

export const updateGroupMemberDetails = (prevContacts,groupId, updatedMemberInfo) => {
    return prevContacts.map(contact => {
        if (contact.type !== "GROUP" || contact.contactPersonOrGroupId !== groupId) return contact;

        const updatedGroupMemberDetails = contact.groupMemberDetails.map(member =>
            member.id === updatedMemberInfo.id
                ? updatedMemberInfo // Replace entire User Object
                : member
        );

        return {
            ...contact,
            groupMemberDetails: updatedGroupMemberDetails
        };
    })
}

export const updateGroupMemberInContactIsSelected = (prevContactDetails, updatedMemberInfo) => {
    const updatedGroupMemberDetails = prevContactDetails.groupMemberDetails.map(member =>
        member.id === updatedMemberInfo.id
            ? updatedMemberInfo // Replace entire User Object
            : member
    );
    return {...prevContactDetails, groupMemberDetails: updatedGroupMemberDetails};
}

export function logout({
                           resetUser,
                           resetContacts,
                           resetRecipientId,
                           resetPreviousContactId,
                           resetSelectedContact,
                           resetStomp,
                           navigate,
                       }) {
    try {
        console.log("Inside new Logout");
        localStorage.removeItem('token');

        // Reset all contexts
        resetUser();
        resetContacts();
        resetRecipientId();
        resetPreviousContactId();
        resetSelectedContact();
        resetStomp();

        // Navigate to login page
        navigate("/login");
    } catch (err) {
        console.error('Logout error:', err);
    }
}