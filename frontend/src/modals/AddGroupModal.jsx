import { createPortal } from "react-dom";
import {useState} from "react";
import {useContactsContext} from "../context/ContactsContext.jsx";
import {useUserContext} from "../context/UserContext.jsx";
import {sendNewGroupRequest} from "../service/service.js";

function IndividualContact({contact, setSelectedContactIds, selectedContactIds}){

    const {user} = useUserContext();

    const handleCheckboxChange = (e)=>{
        const value = Number(e.target.value);
        setSelectedContactIds((prev) => {
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
                    disabled={contact.contactPersonOrGroupId === user.userId}
                    checked={selectedContactIds.includes(contact.contactPersonOrGroupId) || contact.contactPersonOrGroupId === user.userId}
                />
                <label className="form-check-label" htmlFor={`contact-${contact.id}`}>
                    {contact.nickName}
                </label>
            </div>
        </>
    );
}

function AddGroupModal() {

    const {contacts} = useContactsContext();
    const {user} = useUserContext();

    const [groupName, setGroupName] = useState("");
    const [selectedContactIds, setSelectedContactIds] = useState([user.userId]);

    const handleNewName = (e) => {
        setGroupName(e.target.value);
    };

    function handleNewGroupCreation(e) {
        e.preventDefault();
        selectedContactIds.forEach(contactId => {console.log(contactId+" is selected")});
        const addGroupData = {name:groupName,members:selectedContactIds};
        sendNewGroupRequest(addGroupData);
    };

    return createPortal(
        <>
            <form onSubmit={(event)=>handleNewGroupCreation(event)}>
                <div className="modal fade" id="addGroupModal" tabIndex="-1" data-bs-backdrop="static" data-bs-keyboard="false" aria-labelledby="addGroupModalLabel" aria-hidden="true">
                    <div className="modal-dialog modal-dialog-scrollable">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title" id="addGroupModalLabel">Add Group</h5>
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
                                                        onChange={(e)=>handleNewName(e)} required
                                                        value={groupName} />
                                            </div>
                                        </div>
                                    </div>
                                    <div className="accordion-item">
                                        <h2 className="accordion-header">
                                            <button className="accordion-button collapsed" type="button"
                                                    data-bs-toggle="collapse" data-bs-target="#panelsStayOpen-collapseTwo"
                                                    aria-expanded="false" aria-controls="panelsStayOpen-collapseTwo">
                                                Group Members
                                            </button>
                                        </h2>
                                        <div id="panelsStayOpen-collapseTwo" className="accordion-collapse collapse">
                                            <div className="accordion-body">

                                                {contacts.filter(contact=>(contact.type==="USER")).map((contact) => (
                                                    <IndividualContact key={contact.id}
                                                                       selectedContactIds={selectedContactIds}
                                                                       setSelectedContactIds={setSelectedContactIds}
                                                                       contact={contact} />))}
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                <button type="submit" className="btn btn-primary">Add Group</button>
                            </div>
                        </div>
                    </div>
                </div>
            </form>
        </>,
        document.getElementById("modal-root")
    );
}

export default AddGroupModal;
