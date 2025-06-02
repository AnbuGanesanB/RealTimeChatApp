import {createPortal} from "react-dom";
import {useEffect, useState} from "react";
import {useSelectedContactContext} from "../context/SelectedContactContext.jsx";
import {useContactsContext} from "../context/ContactsContext.jsx";
import {sendEditGroupRequest,sendEditContactRequest} from "../service/service.js";
import {useRecipientContext} from "../context/RecipientContext.jsx";
import {useBootstrapModalClose,modalHide} from "../service/utilities.js";
import {Modal} from "bootstrap";

function EditUserContactModal({onClose}){

    const {recipientId, setRecipientId} = useRecipientContext();
    const {contacts, setContacts} = useContactsContext();
    const {selectedContactDetails} = useSelectedContactContext();

    //const oldNickName = selectedContact.nickName;
    const[nickName, setNickName] = useState("");

    useBootstrapModalClose("editUserModal",onClose);

    const handleEditUserDetails = (e)=>{
        e.preventDefault();
        modalHide("editUserModal");
    }

    function handleNewNickName(e) {
        return undefined;
    }

    useEffect(() => {
        setNickName(selectedContactDetails.nickName);
    }, [recipientId]);

    return createPortal(
        <>
            <form onSubmit={(e)=>handleEditUserDetails(e)}>
                <div className="modal fade" id="editUserModal" tabIndex="-1" aria-labelledby="editUserModalLabel" aria-hidden="true">
                    <div className="modal-dialog modal-dialog-scrollable">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title" id="editUserModalLabel">Edit User</h5>
                                <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div className="modal-body">
                                <div className="accordion" id="accordionPanelsStayOpenExample">
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
                                                        value={nickName} />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                <button type="submit" className="btn btn-primary">Update User</button>
                            </div>
                        </div>
                    </div>
                </div>
            </form>
        </>,
        document.getElementById("modal-root")
    );
}

export default EditUserContactModal;