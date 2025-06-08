import {useUserContext} from "../context/UserContext.jsx";
import toputil from '../style/topUtil.module.css';
import StatusSelector from './StatusSelector.jsx';
import {BASE_URL} from "../config.js";

function TopUtil({ onEditSelfProfile }){

    const {user, statusColors} = useUserContext();

    const statusColor = statusColors[user.onlineStatus];

    return (
        <div className={toputil.container}>
            <div className={toputil.toprow}>
                <div className={toputil.avatarWrapper}>
                    {user.dpAvailable ?
                        (<img className={toputil.avatarDp} src={`${BASE_URL}/dp/${user.dpPath}`}
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
            <button className={toputil.editButton} type="button" onClick={onEditSelfProfile}>
                <i className="bi bi-three-dots" />
            </button>
        </div>)
}

export default TopUtil;