import Message from './Message';
import {useEffect, useRef, useState} from "react";
import {useRecipientContext} from "../context/RecipientContext.jsx";
import {useUserContext} from "../context/UserContext.jsx";
import {useSelectedContactContext} from "../context/SelectedContactContext.jsx";
import {useStompClientContext} from "../context/StompClientContext.jsx";
import { fetchMessages } from "../service/service.js"
import styles from '../style/display.module.css';

function ChatDisplay(){

    const [currentMessages, setCurrentMessages] = useState([]);
    const [loading, setLoading] = useState(false);

    const {recipientId,previousContactId} = useRecipientContext();
    const {receivedMessage} = useStompClientContext();
    const {selectedContactDetails} = useSelectedContactContext();
    const {user} = useUserContext();

    /**
     * previousContactId - just to reset the previous contact. Chats loaded for new contact only
     */
    useEffect(() => {
        if (!recipientId) return;

        const loadMessages = async () => {
            setLoading(true);
            try {
                const result = await fetchMessages(previousContactId, recipientId);
                setCurrentMessages(result);
            } catch (error) {
                console.error("Message loading failed:", error.message);
                setCurrentMessages([]);
            } finally {
                setLoading(false); // End loading
            }
        };
        loadMessages();
    },[recipientId]);

    const bottomRef = useRef(null);

    useEffect(() => {
        // Scroll to bottom every time messages change
        if (bottomRef.current) {
            bottomRef.current.scrollIntoView({ behavior: 'auto' }); // or 'smooth' if you prefer
        }
    }, [currentMessages]); // <- re-run on new messages



    /**
     * Append Incoming message to chat-display, if the message is from selected Contact
     */
    useEffect(() => {

        const { indRecipientId, grpRecipientId, senderId } = receivedMessage;

        const isUserMessage = indRecipientId && (senderId === selectedContactDetails.contactPersonOrGroupId || senderId === user.userId);
        const isGroupMessage = grpRecipientId && grpRecipientId === selectedContactDetails.contactPersonOrGroupId;

        if (isUserMessage || isGroupMessage) {
            setCurrentMessages(prevMessages => [...prevMessages, receivedMessage]);
        }

    }, [receivedMessage.timestamp]);


    return(
        <div id="chat-display" className={styles.chatContainer}>
            {loading ? (
                <p>Loading messages...</p>
            ) : (
                currentMessages.map(message => {
                    const isSelf = message.senderId === user.userId;
                    const isUpdateMessage = message.messageType !== "TEXT_MESSAGE";
                    return (
                        <div
                            key={message.id} className={isUpdateMessage ? styles.updateMessage : isSelf ? styles.messageSelf : styles.messageNotSelf}>
                            <Message message={message} />
                        </div>
                    );
                })
            )}
            <div ref={bottomRef} />
        </div>
    )
}

export default ChatDisplay;