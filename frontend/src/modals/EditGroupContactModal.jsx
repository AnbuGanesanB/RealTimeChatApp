import {createPortal} from "react-dom";
import {useEffect, useState} from "react";
import {useSelectedContactContext} from "../context/SelectedContactContext.jsx";
import {useContactsContext} from "../context/ContactsContext.jsx";
import {sendEditGroupRequest,sendEditContactRequest} from "../service/service.js";
import {useRecipientContext} from "../context/RecipientContext.jsx";


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

function EditGroupContactModal(){

    const {recipientId} = useRecipientContext();
    const {contacts} = useContactsContext();
    const {selectedContactDetails} = useSelectedContactContext();

    const [newGroupName, setNewGroupName] = useState("");       //  for New-Group name
    const [oldNickName, setOldNickName] = useState("");
    const [newNickName, setNewNickName] = useState("");
    const [oldMembers, setOldMembers] = useState([]);           // UserDetailDTO
    const [oldMemberIds, setOldMemberIds] = useState([]);       // User-ID's    (old members)
    const [newMemberIds, setNewMemberIds] = useState([]);       // User-ID's    (new members)


    const handleEditGroupDetails = (e)=>{
        e.preventDefault();
        console.log("Edit Triggered:--")
        newMemberIds.forEach(memberId => {console.log(memberId," is selected")});

    }

    useEffect(() => {

        setNewGroupName(selectedContactDetails.contactPersonOrGroupName);

        setOldNickName(selectedContactDetails.nickName);
        setNewNickName(selectedContactDetails.nickName);

        setOldMembers(selectedContactDetails.groupMemberDetails ? selectedContactDetails.groupMemberDetails : []);
        //setNewMembers(selectedContactDetails.groupMemberDetails);

        setOldMemberIds(
            selectedContactDetails.groupMemberDetails
                ? selectedContactDetails.groupMemberDetails.map(member => member.id)
                : []
        );

        setNewMemberIds(
            selectedContactDetails.groupMemberDetails
                ? selectedContactDetails.groupMemberDetails.map(member => member.id)
                : []
        )

        console.log("Try",oldNickName);

    }, [recipientId]);

    const handleNewNickName = (e) => {
        setNewNickName(e.target.value);
    }

    const handleNewGroupName = (e) => {
        setNewGroupName(e.target.value);
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