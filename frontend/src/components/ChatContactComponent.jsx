import {useRecipientContext} from "../context/RecipientContext.jsx";
import { useContactSelection } from "../service/useContactSelection.js";
import styles from "../style/contact.module.css";
import {useUserContext} from "../context/UserContext.jsx";
import avatar from "../style/avatarMini.module.css";
import AvatarComp from "./AvatarComp.jsx";

export function ContactComponent ({contact, tab}) {
    const {recipientId} = useRecipientContext();
    const handleRecipientSelection = useContactSelection();
    const {statusColors} = useUserContext();
    const statusColor = statusColors[contact.onlineStatus];

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
                        <div>Hello: Hello</div>
                        {contact.unreadMessages > 0 && <span className={styles.unreadCounter}>{contact.unreadMessages}</span>}
                        {/*<div>{user.aboutMe || " "}</div>*/}
                    </div>
                </>
            )}
            {tab==="Contacts" && (
                <>
                    <AvatarComp contact={contact} isStatusNeeded={contact.type==="USER"} styles={styles} />
                    <div className={styles.content}>
                        <div>{contact.contactPersonOrGroupName}</div>
                        <div>Hello: Hello</div>
                        {/*<div>{user.aboutMe || " "}</div>*/}
                    </div>
                </>
            )}
            {tab==="Groups" && (
                <>
                    <AvatarComp contact={contact} isStatusNeeded={contact.type==="USER"} styles={styles} />
                    <div className={styles.content}>
                        <div>{contact.contactPersonOrGroupName}</div>
                        <div>Hello: Hello</div>
                        {/*<div>{user.aboutMe || " "}</div>*/}
                    </div>
                </>
            )}
        </div>
    )

}