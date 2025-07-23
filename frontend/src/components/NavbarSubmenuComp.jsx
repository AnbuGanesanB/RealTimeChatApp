import {useRecipientContext} from "../context/RecipientContext.jsx";
import {useNavigate} from "react-router-dom";
import {useSelectedContactContext} from "../context/SelectedContactContext.jsx";
import {useUserContext} from "../context/UserContext.jsx";
import {notifyProfileUpdate,signout} from "../service/service.js";
import styles from '../style/navbar.module.css';
import { logout } from '../service/utilities.js'
import {useStompClientContext} from "../context/StompClientContext.jsx";
import {useContactsContext} from "../context/ContactsContext.jsx";

function NavbarSubmenuComp(){

    const { resetContacts } = useContactsContext();
    const {resetRecipientId, resetPreviousContactId} = useRecipientContext();
    const {resetSelectedContact} = useSelectedContactContext();
    const { resetStomp } = useStompClientContext();
    const {user, resetUser} = useUserContext();

    const navigate = useNavigate();

    const handleSignOut = async () => {
        try {
            await signout(user.userId);
            await notifyProfileUpdate(user.userId);

            logout({
                resetUser,resetContacts,resetRecipientId,resetPreviousContactId,resetSelectedContact,resetStomp,navigate});
        } catch (error) {
            console.error("Error during sign-out flow:", error);
        }
    };

    const handleSettings = () => {
        console.log("Clicked Settings icon ...");
    }

    const submenuOptions = [
        { icon: "bi bi-gear", text: "Settings", onClick: handleSettings},
        { icon: "bi bi-power", text: "Sign out", onClick: handleSignOut}
    ];


    return(
        <>
            <div className={styles.submenu}>
                <ul className={styles.list}>
                    {submenuOptions.map((option, index) => (
                        <li key={index}
                            className={styles.menus}>
                            <button className={styles.menuButton} onClick={option.onClick}>
                                <i className={option.icon}></i>
                                <span>{option.text}</span>
                            </button>
                        </li>
                    ))}
                </ul>
            </div>
        </>
    )
}

export default NavbarSubmenuComp;