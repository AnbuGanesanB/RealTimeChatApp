import {useUserContext} from "../context/UserContext.jsx";
import styles from '../style/topUtil.module.css';
import StatusSelector from './StatusSelector.jsx';
import {BASE_URL} from "../config.js";

function TopUtil({ onEditSelfProfile }){

    const {user, statusColors} = useUserContext();

    const statusColor = statusColors[user.onlineStatus];

    return (
        <div className={styles.container}>
            <div className={styles.toprow}>
                <div className={styles.avatarWrapper}>
                    {user.dpAvailable ?
                        (<img className={styles.avatarDp} src={`${BASE_URL}/dp/${user.dpPath}`}
                        />)
                        : (<div className={styles.initials}>
                            {user.initials}
                        </div>)}
                    <button style={{ backgroundColor: statusColor }} className={styles.statusIndicator}>
                    <StatusSelector/>
                    </button>
                </div>
                <div className={styles.profileInfo}>
                    <div>{user.name}</div>
                    <div>{user.aboutMe || " "}</div>
                </div>
            </div>
            <button className={styles.editButton} type="button" onClick={onEditSelfProfile}>
                <i className="bi bi-three-dots" />
            </button>
        </div>)
}

export default TopUtil;