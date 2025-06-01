import {useUserContext} from "../context/UserContext.jsx";

function AvatarComp({ contact, isStatusNeeded, styles }) {

    const {statusColors} = useUserContext();
    const {dpAvailable, dpPath, initials, onlineStatus} = contact;
    const statusColor = statusColors[onlineStatus];
    return (
        <>
            <div className={styles.avatarWrapper}>
                {dpAvailable
                    ? (<img className={styles.avatarDp} src={`http://localhost:8080/dp/${dpPath}`} />)
                    : (<div className={styles.initials}>
                        {initials}
                    </div>)}
                {isStatusNeeded && <span style={{ backgroundColor: statusColor }} className={styles.statusIndicator} />}
            </div>
        </>
    )
}

export default AvatarComp;