import styles from '../style/topUtil.module.css';
import {notifyProfileUpdate, statusChange} from '../service/service.js'
import {useUserContext} from "../context/UserContext.jsx";

function StatusSelector() {
    const {user, setUser, statusColors} = useUserContext();

    const allStatus = [
        { color: "green", name: "Online", index: 0},
        { color: "red", name: "Do Not Disturb", index: 1},
        { color: "orange", name: "Away", index: 2},
        { color: "gray", name: "Offline", index: 3},
    ];

    const handleStatusChange = async (status) => {
        if (user.onlineStatus === status.name.toString().toLocaleUpperCase()) return;
        const response = await statusChange(user.userId, status.index);
        if(response && response.ok) {
            const updatedProfile = await response.json();
            setUser(prev => ({
                ...updatedProfile,
                isLoggedIn: prev.isLoggedIn
            }));
            await notifyProfileUpdate(user.userId);
        }
    }

    return (
        <div className={styles.statusSelector}>
            <ul className={styles.list}>
                {allStatus.map((status,index) => (
                    <li key={index}
                        className={styles.status}>
                        <button className={styles.statusButton} onClick={()=>handleStatusChange(status)}>
                            <span className={styles.statusColor} style={{ backgroundColor: status.color }}></span>
                            <span className={styles.statusName}>{status.name}</span>
                        </button>
                    </li>
                ))}
            </ul>
        </div>
    )
}

export default StatusSelector;