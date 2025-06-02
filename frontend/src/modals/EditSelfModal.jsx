import {createPortal} from "react-dom";
import {useEffect, useState} from "react";
import {ContactComponent} from "../components/ChatContactComponent.jsx";
import {useUserContext} from "../context/UserContext.jsx";
import {notifyProfileUpdate, sendEditDisplayProfileRequest} from "../service/service.js";
import { useBootstrapModalClose, modalHide } from '../service/utilities.js'
import styles from '../style/EditSelfModal.module.css';

function EditSelfModal({onClose}) {
    const {user, setUser} = useUserContext();

    useBootstrapModalClose("editSelfModal",onClose);

    const [aboutMe, setAboutMe] = useState(user.aboutMe);
    const [name, setName] = useState(user.name);
    const [selectedFile, setSelectedFile] = useState(null);
    const [previewUrl, setPreviewUrl] = useState(user.dpPath);
    const [isDpChanged, setIsDpChanged] = useState(false);

    const handleFileChange = (event) => {
        const file = event.target.files[0];
        setSelectedFile(file);
        const preview = URL.createObjectURL(file);
        setPreviewUrl(preview);
        setIsDpChanged(true);
    };

    const handleFileRemove = (event) => {
        setSelectedFile(null);
        setPreviewUrl(null);
        setIsDpChanged(true);
    }

    const [activeTab, setActiveTab] = useState("Pic");
    const tabs = ["Pic", "AboutMe", "Name"];

    const handleEditDisplayProfile = async (e) => {
        e.preventDefault();
        const formData = new FormData();

        formData.append("profilePic", selectedFile);
        formData.append("aboutMe",aboutMe);
        formData.append("name",name);
        formData.append("userId", user.userId);
        formData.append("isDpChanged",isDpChanged);

        const response = await sendEditDisplayProfileRequest(formData);
        if(response && response.ok) {
            const updatedProfile = await response.json();
            setUser(prev => ({
                ...updatedProfile,
                isLoggedIn: prev.isLoggedIn
            }));
            await notifyProfileUpdate(user.userId);

            modalHide("editSelfModal");
        }
    }

    const handleEditImage = (event) => {
        document.getElementById('dpImageInput').click();
    }


    return createPortal(
        <>
            <form onSubmit={(e)=>handleEditDisplayProfile(e)}>
                <div className="modal fade" id="editSelfModal" tabIndex="-1" aria-labelledby="editSelfModalLabel" aria-hidden="true">
                    <div className="modal-dialog modal-dialog-scrollable">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title" id="editSelfModalLabel">Edit Display Profile</h5>
                                <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div className="modal-body">
                                <div className={styles.modalBody}>
                                    <nav className={styles.customModalTabNav}>
                                        {tabs.map((tab) => (
                                            <button
                                                type="button"
                                                key={tab}
                                                className={`${styles.customTabNavButton} ${activeTab === tab ? styles.customTabNavButtonActive : styles.customTabNavButtonInactive}`}
                                                onClick={() => setActiveTab(tab)}
                                            >
                                                {tab}
                                            </button>
                                        ))}
                                    </nav>
                                    <div className={styles.customModalTabContent}>
                                        {activeTab === "Pic" && (
                                            <div className="profile-modal-tab-pane">
                                                <div className={styles.imageContainer}>

                                                    {previewUrl && !selectedFile && (
                                                        <img className={styles.image} src={`http://localhost:8080/dp/${previewUrl}`} alt="Profile Pic Preview"/>)}
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
                                            </div>
                                            )}
                                        {activeTab === "AboutMe" && (
                                            <div className="profile-modal-tab-pane">
                                            <input className={styles.customModalTextInput} type="text"
                                                       placeholder="About Me.."
                                                       value={aboutMe}
                                                       onChange={(e) => setAboutMe(e.target.value)} />
                                            </div>
                                        )}
                                        {activeTab === "Name" && (
                                            <div className="profile-modal-tab-pane">
                                                <input className={styles.customModalTextInput} type="text"
                                                       placeholder="Your name..."
                                                       value={name}
                                                       onChange={(e) => setName(e.target.value)} />
                                            </div>
                                        )}
                                    </div>
                                </div>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary" data-bs-dismiss="modal">Close
                                </button>
                                <button type="submit" className="btn btn-primary" >Update User</button>
                            </div>
                        </div>
                    </div>
                </div>
            </form>
        </>,
    document.getElementById("modal-root")
    );
}

export default EditSelfModal;