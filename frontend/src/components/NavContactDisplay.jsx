import {useSelectedContactContext} from "../context/SelectedContactContext.jsx";
import styles from '../style/navContactDisplay.module.css';
import {useUserContext} from "../context/UserContext.jsx";
import {BASE_URL} from '../config';

function NavContactDisplay() {

    const {selectedContactDetails} = useSelectedContactContext();
    const {statusColors} = useUserContext();
    const statusColor = statusColors[selectedContactDetails.onlineStatus];


    return (
        <>
            <div className={styles.contactInfo}>
                <div className={styles.avatar}>
                    {selectedContactDetails.dpAvailable ?
                        (<img className={styles.avatarDp} src={`${BASE_URL}/dp/${selectedContactDetails.dpPath}`}
                        />)
                        : (<div className={styles.initials}>
                            {selectedContactDetails.initials}
                        </div>)}

                    {selectedContactDetails.type === "USER" && (
                        <span className={styles.statusIndicator} style={{backgroundColor: statusColor}} />
                    )}
                </div>
                <div className={styles.contactDetails}>
                    {selectedContactDetails.contactPersonOrGroupName === selectedContactDetails.nickName
                        ? (<div className={styles.contactLine1}>
                            {selectedContactDetails.contactPersonOrGroupName}
                        </div>)
                        : (<div className={styles.contactLine1}>
                            {`${selectedContactDetails.nickName} (${selectedContactDetails.contactPersonOrGroupName})`}
                        </div>)}

                    {selectedContactDetails.type === "USER"
                        ? (<div className={styles.contactLine2}>
                            {selectedContactDetails.aboutMe || ""}
                        </div>)
                        : (<div className={styles.contactLine2}>
                            {`${selectedContactDetails.groupMemberDetails.length - selectedContactDetails.removedMemberIds.length} Participants`}
                        </div>)}
                </div>
            </div>
        </>

    )
}

export default NavContactDisplay;