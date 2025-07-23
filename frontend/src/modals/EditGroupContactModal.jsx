import {createPortal} from "react-dom";
import {useEffect, useState} from "react";
import {useSelectedContactContext} from "../context/SelectedContactContext.jsx";
import {useContactsContext} from "../context/ContactsContext.jsx";
import {editGroupContact, editUserContact} from "../service/service.js";
import {useRecipientContext} from "../context/RecipientContext.jsx";
import {modalHide, useBootstrapModalClose} from "../service/utilities.js";
import styles from '../style/EditSelfModal.module.css';
import {useUserContext} from "../context/UserContext.jsx";
import {BASE_URL} from "../config.js";


function NewMemberComp({contact, setNewMemberIds}){

    const handleCheckboxChange = (e)=>{
        const value = Number(e.target.value);
        setNewMemberIds((prev) => {
            const updated = e.target.checked ? [...prev, value] : prev.filter((v) => v !== value);
            return updated;
        });
    };

    return (
        <>
            <div className="form-check form-switch" key={contact.id}>
                <input
                    className="form-check-input"
                    name="contactUserIds"
                    type="checkbox"
                    id={`contact-${contact.id}`}
                    value={contact.contactPersonOrGroupId} role="switch"
                    onChange={(e)=>handleCheckboxChange(e)}
                />
                <label className="form-check-label" htmlFor={`contact-${contact.id}`}>
                    {contact.nickName}
                </label>
            </div>
        </>
    )
}

function OldMemberComp({existingUser, newMemberIds, setNewMemberIds}){

    const handleCheckboxChange = (e)=>{
        const value = Number(e.target.value);
        setNewMemberIds((prev) => {
            const updated = e.target.checked ? [...prev, value] : prev.filter((v) => v !== value);
            return updated;
        });
    };


    return (
        <>
            <div className="form-check form-switch" key={existingUser.id}>
                <input
                    className="form-check-input"
                    name="existingUserIds"
                    type="checkbox"
                    id={`user-${existingUser.id}`}
                    value={existingUser.id} role="switch"
                    onChange={(e)=>handleCheckboxChange(e)}
                    checked={newMemberIds.includes(existingUser.id)}
                />
                <label className="form-check-label" htmlFor={`user-${existingUser.id}`}>
                    {existingUser.name}
                </label>
            </div>
        </>
    )
}

function EditGroupContactModal({onClose}){

    const {recipientId} = useRecipientContext();
    const {contacts} = useContactsContext();
    const {selectedContactDetails} = useSelectedContactContext();
    const {user} = useUserContext();

    const [newGroupName, setNewGroupName] = useState("");       //  for New-Group name
    const [newNickName, setNewNickName] = useState("");
    const [oldMembers, setOldMembers] = useState([]);           // UserDetailDTO
    const [oldMemberIds, setOldMemberIds] = useState([]);       // User-ID's    (old members)
    const [newMemberIds, setNewMemberIds] = useState([]);       // User-ID's    (new members)

    const [groupNameError, setGroupNameError] = useState(null);
    const [contactNameError, setContactNameError] = useState(null);
    const [picFormatError, setPicFormatError] = useState(null);
    const [picSizeError, setPicSizeError] = useState(null);
    const [groupMemberError, setGroupMemberError] = useState(null);

    const [selectedFile, setSelectedFile] = useState(null);
    const [previewUrl, setPreviewUrl] = useState(selectedContactDetails.dpPath);
    const oldDpPath = selectedContactDetails.dpPath;
    const removedMemberIds = selectedContactDetails.removedMemberIds;

    useBootstrapModalClose("editGroupModal",onClose);

    const handleEditGroupDetails = async (e) => {
        e.preventDefault();
        newMemberIds.forEach(memberId => {console.log(memberId," is selected")});

        const isDpChanged = oldDpPath !== previewUrl;

        const editedGroupContactData = new FormData();

        editedGroupContactData.append("contactId",selectedContactDetails.id);
        editedGroupContactData.append("newGroupName",newGroupName);
        editedGroupContactData.append("newNickName",newNickName);
        newMemberIds.forEach(memberId => {editedGroupContactData.append("newMemberIds",memberId)});
        editedGroupContactData.append("profilePic", selectedFile);
        editedGroupContactData.append("isDpChanged",isDpChanged);

        try{
            setGroupNameError(null);
            setContactNameError(null);
            setPicFormatError(null);
            setPicSizeError(null);
            setGroupMemberError(null);
            await editGroupContact(editedGroupContactData);
            modalHide("editGroupModal");
        }catch(error){
            const actualErrorMessage = error.message;
            if(actualErrorMessage.includes("Group Name")) setGroupNameError(actualErrorMessage);
            else if(actualErrorMessage.includes("Nickname")) setContactNameError(actualErrorMessage);
            else if(actualErrorMessage.includes("type")) setPicFormatError(actualErrorMessage);
            else if(actualErrorMessage.includes("size")) setPicSizeError(actualErrorMessage);
            else if(actualErrorMessage.includes("member")) setGroupMemberError(actualErrorMessage);
        }
    }

    useEffect(() => {

        setNewGroupName(selectedContactDetails.contactPersonOrGroupName);
        setNewNickName(selectedContactDetails.nickName);

        const filteredMembers = selectedContactDetails.groupMemberDetails
            ? selectedContactDetails.groupMemberDetails
                .filter((member) => !removedMemberIds.includes(member.id))
            : [];

        setOldMembers(filteredMembers);
        setOldMemberIds(filteredMembers.map(member => member.id));
        setNewMemberIds(filteredMembers.map(member => member.id));

    }, [recipientId]);

    const handleNewNickName = (e) => {
        setNewNickName(e.target.value);
    }

    const handleNewGroupName = (e) => {
        setNewGroupName(e.target.value);
    }

    const handleFileChange = (event) => {
        const file = event.target.files[0];
        setSelectedFile(file);
        const preview = URL.createObjectURL(file);
        console.log("preview URL:",preview);
        setPreviewUrl(preview);
    };

    const handleFileRemove = (event) => {
        setSelectedFile(null);
        setPreviewUrl(null);
    }

    const handleEditImage = (event) => {
        document.getElementById('dpImageInput').click();
    }

    return createPortal(
        <>
            <form onSubmit={(e)=>handleEditGroupDetails(e)}>
                <div className="modal fade" id="editGroupModal" tabIndex="-1" aria-labelledby="editGroupModalLabel" aria-hidden="true">
                    <div className="modal-dialog modal-dialog-scrollable">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title" id="editGroupModalLabel">Edit Group</h5>
                                <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div className="modal-body">
                                <div className="accordion" id="accordionPanelsStayOpenExample">
                                    <div className="accordion-item">
                                        <h2 className="accordion-header">
                                            <button className="accordion-button" type="button" data-bs-toggle="collapse"
                                                    data-bs-target="#panelsStayOpen-collapseOne" aria-expanded="true"
                                                    aria-controls="panelsStayOpen-collapseOne">
                                                Group Name
                                            </button>
                                        </h2>
                                        <div id="panelsStayOpen-collapseOne" className="accordion-collapse collapse show">
                                            <div className="accordion-body">
                                                <input  type="text"
                                                        placeholder="Group Name..."
                                                        id="groupName"
                                                        name="groupName"
                                                        onChange={(e)=>handleNewGroupName(e)} required
                                                        value={newGroupName} />
                                                {groupNameError && (
                                                    <div style={{color:'red'}}>
                                                        {groupNameError}
                                                    </div>
                                                )}
                                            </div>
                                        </div>
                                    </div>
                                    <div className="accordion-item">
                                        <h2 className="accordion-header">
                                            <button className="accordion-button" type="button" data-bs-toggle="collapse"
                                                    data-bs-target="#panelsStayOpen-collapseOne" aria-expanded="true"
                                                    aria-controls="panelsStayOpen-collapseOne">
                                                Display Picture
                                            </button>
                                        </h2>
                                        <div id="panelsStayOpen-collapseOne" className="accordion-collapse collapse show">
                                            <div className="accordion-body">
                                                <div className={styles.imageContainer}>

                                                    {previewUrl && !selectedFile && (
                                                        <img className={styles.image} src={`${BASE_URL}/dp/${previewUrl}`} alt="Profile Pic Preview"/>)}
                                                    {previewUrl && selectedFile && (
                                                        <img className={styles.image} src={`${previewUrl}`} alt="Profile Pic Preview"/>)}
                                                    {!previewUrl && !selectedFile && (
                                                        <i className={`bi bi-instagram ${styles.noImage}`}/>)}

                                                    <button className={styles.imageRemove} type="button"
                                                            onClick={handleFileRemove}>X
                                                    </button>
                                                    <div className="upload-wrapper">
                                                        <button type="button" id="editIconBtn" className={styles.imageEdit} onClick={handleEditImage}>
                                                            <i className="bi bi-pencil"/>
                                                        </button>

                                                        <input type="file" id="dpImageInput" className={styles.hiddenFileInput}
                                                               onChange={handleFileChange} accept="image/*"/>
                                                    </div>
                                                </div>
                                                {picFormatError && (
                                                    <div style={{color:'red'}}>
                                                        {picFormatError}
                                                    </div>
                                                )}
                                                {picSizeError && (
                                                    <div style={{color:'red'}}>
                                                        {picSizeError}
                                                    </div>
                                                )}
                                            </div>
                                        </div>
                                    </div>
                                    <div className="accordion-item">
                                        <h2 className="accordion-header">
                                            <button className="accordion-button" type="button" data-bs-toggle="collapse"
                                                    data-bs-target="#panelsStayOpen-collapseTwo" aria-expanded="true"
                                                    aria-controls="panelsStayOpen-collapseTwo">
                                                Contact Name
                                            </button>
                                        </h2>
                                        <div id="panelsStayOpen-collapseTwo" className="accordion-collapse collapse show">
                                            <div className="accordion-body">
                                                <input  type="text"
                                                        placeholder="Contact Nick Name..."
                                                        id="nickName"
                                                        name="nickName"
                                                        onChange={(e)=>handleNewNickName(e)} required
                                                        value={newNickName} />
                                                {contactNameError && (
                                                    <div style={{color:'red'}}>
                                                        {contactNameError}
                                                    </div>
                                                )}
                                            </div>
                                        </div>
                                    </div>
                                    <div className="accordion-item">
                                        <h2 className="accordion-header">
                                            <button className="accordion-button collapsed" type="button"
                                                    data-bs-toggle="collapse" data-bs-target="#panelsStayOpen-collapseThree"
                                                    aria-expanded="false" aria-controls="panelsStayOpen-collapseThree">
                                                Group Members
                                            </button>
                                        </h2>
                                        <div id="panelsStayOpen-collapseThree" className="accordion-collapse collapse">
                                            <div className="accordion-body">

                                                {oldMembers.map((oldMember) => (
                                                    <OldMemberComp key={oldMember.id}
                                                                    newMemberIds={newMemberIds}
                                                                    setNewMemberIds={setNewMemberIds}
                                                                    existingUser={oldMember} />))}
                                                {groupMemberError && (
                                                    <div style={{color:'red'}}>
                                                        {groupMemberError}
                                                    </div>
                                                )}
                                            </div>
                                        </div>
                                    </div>
                                    <div className="accordion-item">
                                        <h2 className="accordion-header">
                                            <button className="accordion-button collapsed" type="button"
                                                    data-bs-toggle="collapse" data-bs-target="#panelsStayOpen-collapseFour"
                                                    aria-expanded="false" aria-controls="panelsStayOpen-collapseFour">
                                                New Members
                                            </button>
                                        </h2>
                                        <div id="panelsStayOpen-collapseFour" className="accordion-collapse collapse">
                                            <div className="accordion-body">

                                                {contacts.filter(contact=> contact.type === "USER" && !oldMemberIds.includes(contact.contactPersonOrGroupId))
                                                    .map((contact) => (
                                                    <NewMemberComp key={contact.id}
                                                                        setNewMemberIds={setNewMemberIds}
                                                                        contact={contact} />))}
                                                {groupMemberError && (
                                                    <div style={{color:'red'}}>
                                                        {groupMemberError}
                                                    </div>
                                                )}
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                <button type="submit" className="btn btn-primary">Update Group</button>
                            </div>
                        </div>
                    </div>
                </div>
            </form>
        </>,
        document.getElementById("modal-root")
    );
}

export default EditGroupContactModal;