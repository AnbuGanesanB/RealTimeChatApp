import {useState} from "react";
import {useRecipientContext} from "../context/RecipientContext.jsx";
import Message from "./Message.jsx";
import {UploadMessageRequest} from "../service/service.js";
import {useUserContext} from "../context/UserContext.jsx";
import {useSelectedContactContext} from "../context/SelectedContactContext.jsx";

function MessageUtil() {

    const {recipientId} = useRecipientContext();
    const {user} = useUserContext();
    const {selectedContactDetails} = useSelectedContactContext();

    const [currentMessage,setCurrentMessage] = useState("");
    const [selectedFiles, setSelectedFiles] = useState([]);

    const handleMessageSubmit = (event) => {
        event.preventDefault();
        if(!currentMessage && selectedFiles.length===0) return;

        const formData = new FormData();
        selectedFiles.forEach(file => {
            formData.append("files", file);
        });
        formData.append("contactId",recipientId);
        formData.append("content",currentMessage);

        UploadMessageRequest(formData);
        setCurrentMessage("");
        setSelectedFiles([]);
    };


    const handleFileChange = (e) => {
        const files = Array.from(e.target.files);
        setSelectedFiles((prev) => [...prev, ...files]);
    };

    const removeFile = (indexToRemove) => {
        setSelectedFiles((prev) =>
            prev.filter((_, index) => index !== indexToRemove)
        );
    };

    return (
            <div className="message-util p-2">
                {selectedFiles.length > 0 && (
                    <div className="mb-2 d-flex flex-wrap gap-2">
                        {selectedFiles.map((file, index) => (
                            <div key={index} className="card p-2" style={{ width: "12rem" }}>
                                <div className="d-flex justify-content-between align-items-center mb-1">
                                    <small className="text-truncate w-75" title={file.name}>
                                        {file.name}
                                    </small>
                                    <button
                                        type="button"
                                        className="btn-close btn-sm"
                                        onClick={() => removeFile(index)}
                                    ></button>
                                </div>
                                <small className="text-muted">{(file.size / 1024).toFixed(1)} KB</small>
                            </div>
                        ))}
                    </div>
                )}

                <form onSubmit={(event)=>handleMessageSubmit(event)} className="w-100">
                    <div className="container-fluid">
                        <div className="row align-items-center">
                            <div className="col-10 d-flex justify-content-start">
                                <input className="message-input"
                                       value={currentMessage}
                                       onChange={(e) => setCurrentMessage(e.target.value)}
                                       placeholder="Type your message..."/>
                            < /div>
                            <div className="col-2 d-flex justify-content-start">
                                <div className="message-icons d-flex align-items-center gap-2">
                                    <div>
                                        <label htmlFor="fileInput" className="btn btn-primary btn-sm m-0">
                                            <i className="bi bi-paperclip"></i>
                                        </label>
                                        <input
                                            id="fileInput"
                                            className="d-none"
                                            type="file"
                                            multiple
                                            onChange={handleFileChange}
                                        />
                                    </div>
                                    <div>
                                        <button type="submit"
                                                className="btn btn-primary btn-sm"
                                                disabled={
                                                    !recipientId ||
                                                    (
                                                        Array.isArray(selectedContactDetails?.removedMemberIds) &&
                                                        selectedContactDetails.removedMemberIds.includes(user.userId)
                                                    )
                                                }
                                        >
                                            <i className="bi bi-chevron-double-right"></i>
                                        </button>
                                    </div>
                                </div>
                            < /div>
                        < /div>
                    < /div>
                </form>
            </div>

        );
}

export default MessageUtil;