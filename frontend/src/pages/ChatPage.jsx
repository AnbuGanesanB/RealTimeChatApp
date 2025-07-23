import TopUtil from "../components/TopUtil.jsx";
import NavBar from "../components/NavBar.jsx";
import ChatDisplay from "../components/ChatDisplay.jsx";
import MessageUtil from "../components/MessageUtil.jsx";
import './../style/Chatpage.css'
import ChatSelectionComponent from "../components/ChatSelectionComponent.jsx";
import {useRecipientContext} from "../context/RecipientContext.jsx";
import WelcomeDisplayComp from "../components/WelcomeDisplayComp.jsx";
import NewContactsComp from "../components/NewContactsComp.jsx";
import AddGroupModal from "../modals/AddGroupModal.jsx";
import EditGroupContactModal from "../modals/EditGroupContactModal.jsx";
import EditUserContactModal from "../modals/EditUserContactModal.jsx";
import EditSelfModal from "../modals/EditSelfModal.jsx";
import { useSideEffects } from "./SideEffects.jsx"
import {useEffect, useState} from "react";
import {useUserContext} from "../context/UserContext.jsx";
import {useSelectedContactContext} from "../context/SelectedContactContext.jsx";
import {useNavigate} from "react-router-dom";

function ChatPage(){
    const {recipientId} = useRecipientContext();
    const {user} = useUserContext();
    const {selectedContactDetails} = useSelectedContactContext();
    const navigate = useNavigate();

    const [showEditSelfModal, setShowEditSelfModal] = useState(false);
    const [showAddGroupModal, setShowAddGroupModal] = useState(false);
    const [showEditGroupContactModal, setShowEditGroupContactModal] = useState(false);
    const [showEditUserContactModal, setShowEditUserContactModal] = useState(false);

    useEffect(() => {
        if (user?.userId === 0) {
            console.log("You are logged out");
            navigate("/login");
        }
    }, [user?.userId, navigate]);

    /*if (user.userId === 0 || !user.isLoggedIn) {
        navigate("/login");
        return null;
    }*/

    useSideEffects();

    return (
        <div className="chat-page">
            <div className="left-layout">
                <TopUtil onEditSelfProfile={() => setShowEditSelfModal(true)}/>
                <ChatSelectionComponent />
            </div>
            <div className="right-layout">
                <NavBar onAddGroup={() => setShowAddGroupModal(true)}
                        onEditGroupContact={() => {
                            console.log("Trying Edit Group:",selectedContactDetails);
                            if (!(selectedContactDetails?.removedMemberIds?.includes(user.userId))) {
                                setShowEditGroupContactModal(true);
                            }}}
                        onEditUserContact={() => setShowEditUserContactModal(true)}/>
                {recipientId ? <ChatDisplay /> : <WelcomeDisplayComp />}
                <MessageUtil />
            </div>
            {/* Placing all the Modals related to Chat page here */}
            {showEditSelfModal && (
                <EditSelfModal onClose={() => setShowEditSelfModal(false)}/>)}
            {showAddGroupModal && (
                <AddGroupModal onClose={() => setShowAddGroupModal(false)}/>)}
            {showEditGroupContactModal && (
                <EditGroupContactModal onClose={() => setShowEditGroupContactModal(false)}/>)}
            {showEditUserContactModal && (
                <EditUserContactModal onClose={() => setShowEditUserContactModal(false)}/>)}
        </div>)
}

export default ChatPage;