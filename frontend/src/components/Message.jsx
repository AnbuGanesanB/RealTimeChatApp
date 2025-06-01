import styles from '../style/message.module.css';
import {useUserContext} from "../context/UserContext.jsx";
import avatar from "../style/avatarMini.module.css";
import AvatarComp from "../components/AvatarComp";
import contactCss from "../style/contact.module.css";
import {useSelectedContactContext} from "../context/SelectedContactContext.jsx";
import {useEffect, useMemo} from "react";

function Message({message}){

    const {user} = useUserContext();
    const {selectedContactDetails} = useSelectedContactContext();
    const files = message.files;

    let avatarContact = null;

    if (message.senderId === user.userId) {
        avatarContact = user;
    } else if (message.indRecipientId) {
        avatarContact = selectedContactDetails;
    } else {
        avatarContact = selectedContactDetails.groupMemberDetails?.find(
            member => member.id === message.senderId
        );
    }

    useEffect(() => {
        console.log("selectedContactDetails changed", selectedContactDetails);
    }, [selectedContactDetails]);

    useEffect(() => {
        console.log("groupMemberDetails changed", selectedContactDetails.groupMemberDetails);
    }, [selectedContactDetails.groupMemberDetails]);


    /*const avatarContact = useMemo(() => {
        if (message.senderId === user.userId) return user;
        if (message.indRecipientId) return selectedContactDetails;
        return selectedContactDetails.groupMemberDetails?.find(
            member => member.id === message.senderId
        );
    }, [message.senderId, message.indRecipientId, user, selectedContactDetails, selectedContactDetails.groupMemberDetails]);
*/
    function renderFile(file,index){

        const isImage = file.originalFileName.match(/\.(jpg|jpeg|png|gif|webp)$/i);

        return (
            <>
                <div className={styles.image} key={index}>
                    {isImage ? (
                        <>
                            <a
                                href={`http://localhost:8080/files/preview/${file.uniqueFileName}`}
                                target="_blank"
                                rel="noopener noreferrer"
                            >
                                <img className={styles.image}
                                    src={`http://localhost:8080/files/preview/${file.uniqueFileName}`}
                                    style={{ maxWidth: "200px", marginBottom: "8px" }}
                                />
                            </a>
                            <br />
                            <a href={`http://localhost:8080/files/download/${file.uniqueFileName}`} download={file.originalFileName}>
                                <i className={`bi bi-download ${styles.imageDownload}`} />
                            </a>
                        </>
                    ) : (
                        <a className={styles.fileDownload} href={`http://localhost:8080/files/download/${file.uniqueFileName}`} download={file.originalFileName}>
                            {file.originalFileName}
                        </a>
                    )}
                </div>
            </>
        )
    }

    function formatTimestamp(timestamp) {
        const date = new Date(timestamp);
        return `${date.getDate()}-${date.toLocaleString('en-US', { month: 'short' })}; ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
    }

    return (
        <>
            <div className={`${styles.container} ${message.senderId === user.userId ? styles.messageSelf : styles.messageNotSelf}`}>
                <div className={avatar.avatarWrapper}>
                    {avatarContact && (
                        <AvatarComp
                            contact={avatarContact}
                            isStatusNeeded={message.senderId !== user.userId}
                            styles={avatar}
                        />
                    )}
                </div>

                <div className={styles.messageBody}>
                    <div className={styles.messageMeta}>
                        {`${message.sender} | ${formatTimestamp(message.timestamp)}`}
                    </div>
                    <div className={styles.messageContent}>
                        <div className={styles.para}>{message.content}</div>
                        {files && files.length > 0 && files.map((file,index) => renderFile(file,index))}
                    </div>
                </div>
            </div>

        </>
        )
}

export default Message;