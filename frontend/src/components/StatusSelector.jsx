import toputil from '../style/topUtil.module.css';
import {notifyProfileUpdate, statusChange} from '../service/service.js'
import {useUserContext} from "../context/UserContext.jsx";

function StatusSelector() {
    const {user, setUser} = useUserContext();

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
        <div className={toputil.statusSelector}>
            <ul className={toputil.list}>
                {allStatus.map((status,index) => (
                    <li key={index}
                        className={toputil.status}>
                        <button className={toputil.statusButton} onClick={()=>handleStatusChange(status)}>
                            <span className={toputil.statusColor} style={{ backgroundColor: status.color }}></span>
                            <span className={toputil.statusName}>{status.name}</span>
                        </button>
                        {/*{status.replaceAll('_', ' ')}*/}
                    </li>
                ))}
            </ul>
        </div>
    )
}

export default StatusSelector;