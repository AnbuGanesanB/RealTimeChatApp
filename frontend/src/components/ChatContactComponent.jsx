import {useRecipientContext} from "../context/RecipientContext.jsx";
import { useContactSelection } from "../service/useContactSelection.js";
import styles from "../style/contact.module.css";
import {useUserContext} from "../context/UserContext.jsx";
import avatar from "../style/avatarMini.module.css";
import AvatarComp from "./AvatarComp.jsx";

export function ContactComponent ({contact, tab}) {
    const {recipientId} = useRecipientContext();
    const handleRecipientSelection = useContactSelection();
    const {user,statusColors} = useUserContext();
    const statusColor = statusColors[contact.onlineStatus];
    const isSelfMessage = contact.lastMessageSenderId === user.userId;

    return (
        <div className={`
            ${styles.contactCard} 
            //${contact.unreadMessages > 0 ? styles.unread : ""} 
            ${recipientId === contact.id ? styles.selected : ""}`}

             onClick={() => handleRecipientSelection(contact)}>
            {tab==="Chats" && (
                <>
                    <AvatarComp contact={contact} isStatusNeeded={contact.type==="USER"} styles={styles} />
                    <div className={styles.content}>
                        <div>{contact.contactPersonOrGroupName}</div>
                        {contact.lastMessageFromUser ? <div className={styles.contentRow}>{`${isSelfMessage ? 'You' : contact.lastMessageFromUser}: ${contact.lastMessageContent}`}</div> : <div>Send First Message</div>}
                        {contact.unreadMessages > 0 && <span className={styles.unreadCounter}>{contact.unreadMessages}</span>}
                    </div>
                </>
            )}
            {tab==="Contacts" && (
                <>
                    <AvatarComp contact={contact} isStatusNeeded={contact.type==="USER"} styles={styles} />
                    <div className={styles.content}>
                        <div>{contact.nickName === contact.contactPersonOrGroupName
                            ? contact.nickName
                            : `${contact.nickName} (${contact.contactPersonOrGroupName})`}</div>
                        <div className={styles.contentRow}>{contact.aboutMe ? contact.aboutMe : contact.lastMessageFromUser ? `${isSelfMessage ? 'You' : contact.lastMessageFromUser}: ${contact.lastMessageContent}` : 'Send first Message'}</div>
                        {contact.unreadMessages > 0 && <span className={styles.unreadCounter}>{contact.unreadMessages}</span>}
                    </div>
                </>
            )}
            {tab==="Groups" && (
                <>
                    <AvatarComp contact={contact} isStatusNeeded={contact.type==="USER"} styles={styles} />
                    <div className={styles.content}>
                        <div>{contact.nickName === contact.contactPersonOrGroupName
                            ? contact.nickName
                            : `${contact.nickName} (${contact.contactPersonOrGroupName})`}</div>
                        <div className={styles.contentRow}>{`${contact.groupMemberDetails.length - contact.removedMemberIds.length} Active Members`}</div>
                        {contact.unreadMessages > 0 && <span className={styles.unreadCounter}>{contact.unreadMessages}</span>}
                    </div>
                </>
            )}
        </div>
    )

}