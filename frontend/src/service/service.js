import {BASE_URL} from '../config';

const getToken = () => {
    return localStorage.getItem("token");
};

export const sendNewContactRequest = async (senderId, contactId) => {
    console.log("sendNewContactRequest senderId : " + senderId);
    console.log("sendNewContactRequest contactId : " + contactId);

    try {
        const response = await fetch(`${BASE_URL}/addcontact`, {
            method: "POST",
            headers: { "Content-Type": "application/json", Authorization: `Bearer ${getToken()}` },
            body: JSON.stringify({
                senderId: senderId,
                contactPersonId: contactId
            }),
        });

        if (!response.ok) {
            throw new Error("Response failed.");
        }

    } catch (error) {
        console.error("Error:", error.message);
        alert("Req. failed! Please try again.");
    }
}

export const sendNewGroupRequest = async (addGroupData) => {
    try {
        const response = await fetch(`${BASE_URL}/createGroup`, {
            method: "POST",
            headers: { "Content-Type": "application/json", Authorization: `Bearer ${getToken()}` },
            body: JSON.stringify(addGroupData),
        });

        if (!response.ok) {
            throw new Error("Group Creation failed.");
        }

    } catch (error) {
        console.error("Error:", error.message);
        alert("Group Creation - failed! Please try again.");
    }
}
/*

export const sendEditGroupRequest = async (editGroupData) => {
    try {
        const response = await fetch(`${BASE_URL}/editgroup`, {
            method: "POST",
            headers: { "Content-Type": "application/json", Authorization: `Bearer ${getToken()}` },
            body: JSON.stringify(editGroupData),
        });

        if (!response.ok) {
            throw new Error("Group Modification failed.");
        }

    } catch (error) {
        console.error("Error:", error.message);
        alert("Group Modification - failed! Please try again.");
    }
}

export const sendEditContactRequest = async (editContactData) => {
    try {
        const response = await fetch(`${BASE_URL}/editcontact`, {
            method: "POST",
            headers: { "Content-Type": "application/json", Authorization: `Bearer ${getToken()}` },
            body: JSON.stringify(editContactData),
        });

        if (!response.ok) {
            throw new Error("Contact Modification failed.");
        }

    } catch (error) {
        console.error("Error:", error.message);
        alert("Contact Modification - failed! Please try again.");
    }
}
*/

export const UploadMessageRequest = async (formData) => {
    try {
        const response = await fetch(`${BASE_URL}/uploadMessage`, {
            method: "POST",
            headers: { Authorization: `Bearer ${getToken()}` },
            body: formData
        });

        if (!response.ok) {
            throw new Error("Message Upload failed.");
        }

    } catch (error) {
        console.error("Error:", error.message);
        alert("Message Upload - failed! Please try again.");
    }
}

/**
 * OldContactId - To trigger an Updated last contact details through WS
 * NewContactId - Fetches the chats on the contact(me and user/group)
 */
export const fetchMessages = async (oldContactId, recipientId) => {
    try {
        const response = await fetch(`${BASE_URL}/chats`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json", Authorization: `Bearer ${getToken()}`
            },
            body: JSON.stringify({
                oldContactId: oldContactId,
                newContactId: recipientId,
            }),
        });

        if (!response.ok) {
            throw new Error("Message Fetching failed!");
        }

        const result = await response.json();
        console.log("Fetch Successful:", result);

        return result;
    } catch (error) {
        console.error("Error:", error.message);
    }
};

/**
 * Edit Contact name(nickname) & fires an Updated contact by WS
 */
export const editUserContact = async (editedUserContactData) => {
    try {
        const response = await fetch(`${BASE_URL}/editUser`, {
            method: "POST",
            headers: {
                Authorization: `Bearer ${getToken()}`
            },
            body: editedUserContactData
        });

        if (!response.ok) {
            throw new Error("Edit Nickname failed!");
        }
    } catch (error) {
        console.error("Error:", error.message);
    }
};

/**
 * Edit Group Contact
 * If (name(nickname)) changes, fires an Updated contact by WS for Me.
 * If common details (Grp name, DP, Members(add/remove)) changes,
 * fires an updated contact for all applicable members.
 */
export const editGroupContact = async (editedGroupContactData) => {
    try {
        const response = await fetch(`${BASE_URL}/editGroup`, {
            method: "POST",
            headers: {
                Authorization: `Bearer ${getToken()}`
            },
            body: editedGroupContactData
        });

        if (!response.ok) {
            throw new Error("Edit Group failed!");
        }
    } catch (error) {
        console.error("Error:", error.message);
    }
};

/**
 * Notify about My update to Other users
 */
export const notifyProfileUpdate = async (userId) => {
    try {
        const response = await fetch(`${BASE_URL}/profileupdate`, {
            method: "POST",
            headers: { "Content-Type": "application/json", Authorization: `Bearer ${getToken()}` },
            body: JSON.stringify({ loginUserId: userId }),
        });

        if (!response.ok) {
            throw new Error("Failed to send Profile update to contacts.");
        }
    } catch (error) {
        console.error("Profile updation sending error:", error.message);
    }
};

export const signout = async (userId) => {
    try {
        const response = await fetch(`${BASE_URL}/signout`, {
            method: "POST",
            headers: { "Content-Type": "application/json", Authorization: `Bearer ${getToken()}` },
            body: JSON.stringify({ loginUserId: userId }),
        });

        if (!response.ok) {
            throw new Error("SignOut failed!");
        }
    } catch (error) {
        console.error("Signout error:", error.message);
    }
};

/**
 * Edit My profile details (Profile Pic, About Me, My Name)
 */
export const sendEditDisplayProfileRequest = async (displayProfileData) => {
    try {
        const response = await fetch(`${BASE_URL}/editDisplayProfile`, {
            method: "POST",
            headers: { Authorization: `Bearer ${getToken()}` },
            body: displayProfileData
        });

        if (!response.ok) {
            throw new Error("DP profile change failed.");
        }
        return response;

    } catch (error) {
        console.error("Error:", error.message);
        alert("DP Profile change - failed! Please try again.");
    }
}

/**
 * Fetches My Details into browser
 */
export const fetchUser = async () => {
    try {
        const response = await fetch(`${BASE_URL}/fetchUser`, {
            method: "GET",
            headers: { "Content-Type": "application/json", Authorization: `Bearer ${getToken()}` }
            //body: JSON.stringify({ loggedUserId: userId }),
        });

        if (!response.ok) {
            throw new Error("Refresh failed!");
        }

        return await response.json();

    } catch (error) {
        console.error("Refresh error:", error.message);
    }
};

/**
 * Change my Online Status(Online/Offline/..)
 */
export const statusChange = async (userId,statusIndex) => {
    try {
        const response = await fetch(`${BASE_URL}/status`, {
            method: "POST",
            headers: { "Content-Type": "application/json", Authorization: `Bearer ${getToken()}` },
            body: JSON.stringify({ loggedUserId: userId, statusIndex: statusIndex }),
        });

        if (!response.ok) {
            throw new Error("status change failed!");
        }

        return response;
    } catch (error) {
        console.error("status change error:", error.message);
    }
};