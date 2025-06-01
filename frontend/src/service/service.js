export const sendNewContactRequest = async (senderId, contactId) => {
    console.log("sendNewContactRequest senderId : " + senderId);
    console.log("sendNewContactRequest contactId : " + contactId);

    const newContactMeta = {senderId: senderId, contactPersonId: contactId};
    try {
        const response = await fetch("http://localhost:8080/addcontact", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(newContactMeta),
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
        const response = await fetch("http://localhost:8080/creategroup", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
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

export const sendEditGroupRequest = async (editGroupData) => {
    try {
        const response = await fetch("http://localhost:8080/editgroup", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
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
        const response = await fetch("http://localhost:8080/editcontact", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
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

/*export const UploadFileRequest = async (formData) => {
    try {
        const response = await fetch("http://localhost:8080/uploadFiles", {
            method: "POST",
            body: formData
        });

        if (!response.ok) {
            throw new Error("File Upload failed.");
        }

    } catch (error) {
        console.error("Error:", error.message);
        alert("File Upload - failed! Please try again.");
    }
}*/

export const UploadMessageRequest = async (formData) => {
    try {
        const response = await fetch("http://localhost:8080/uploadMessage", {
            method: "POST",
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

export const fetchMessages = async (oldContactId, recipientId) => {
    try {
        const response = await fetch("http://localhost:8080/chats", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
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

export const notifyProfileUpdate = async (userId) => {
    try {
        const response = await fetch("http://localhost:8080/profileupdate", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
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
        const response = await fetch("http://localhost:8080/signout", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ loginUserId: userId }),
        });

        if (!response.ok) {
            throw new Error("SignOut failed!");
        }
    } catch (error) {
        console.error("Signout error:", error.message);
    }
};

export const sendEditDisplayProfileRequest = async (displayProfileData) => {
    try {
        const response = await fetch("http://localhost:8080/editDisplayProfile", {
            method: "POST",
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

export const refresh = async (userId) => {
    try {
        const response = await fetch("http://localhost:8080/refresh", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ loggedUserId: userId }),
        });

        if (!response.ok) {
            throw new Error("Refresh failed!");
        }

        return response;
    } catch (error) {
        console.error("Refresh error:", error.message);
    }
};

export const statusChange = async (userId,statusIndex) => {
    try {
        const response = await fetch("http://localhost:8080/status", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
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