import {useUserContext} from "../context/UserContext.jsx";
import toputil from '../style/topUtil.module.css';
import StatusSelector from './StatusSelector.jsx';

function TopUtil(){

    const {user} = useUserContext();
    const statusColors = {
        ONLINE: '#28a745',
        DO_NOT_DISTURB: '#dd0f0f',
        AWAY: '#ffc107',
        OFFLINE: '#9e9e9e',
    };
    const statusColor = statusColors[user.onlineStatus];

    return (
        <div className={toputil.container}>
            <div className={toputil.toprow}>
                <div className={toputil.avatarWrapper}>
                    {user.dpAvailable ?
                        (<img className={toputil.avatarDp} src={`http://localhost:8080/dp/${user.dpPath}`}
                        />)
                        : (<div className={toputil.initials}>
                            {user.initials}
                        </div>)}
                    <button style={{ backgroundColor: statusColor }} className={toputil.statusIndicator}>
                    <StatusSelector/>
                    </button>
                </div>
                <div className={toputil.profileInfo}>
                    <div>{user.name}</div>
                    <div>{user.aboutMe || " "}</div>
                </div>
            </div>
            <button className={toputil.editButton} type="button" data-bs-toggle="modal" data-bs-target="#editSelfModal">
                <i className="bi bi-three-dots" />
            </button>
        </div>)
}

export default TopUtil;