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

function ChatPage(){
    const {recipientId} = useRecipientContext();
    useSideEffects();
    return (
        <div className="chat-page">
            <div className="left-layout">
                <TopUtil />
                <ChatSelectionComponent />
            </div>
            <div className="right-layout">
                <NavBar />
                {recipientId ? <ChatDisplay /> : <WelcomeDisplayComp />}
                <MessageUtil />
            </div>
            {/* Placing all the Modals related to Chat page here */}
            <AddGroupModal />
            <EditGroupContactModal />
            <EditUserContactModal />
            <EditSelfModal />
        </div>)
}

export default ChatPage;