import SignOutComponent from "./SignOutComponent.jsx";
import {useSelectedContactContext} from "../context/SelectedContactContext.jsx";
import NavbarSubmenuComp from "./NavbarSubmenuComp.jsx";
import NavContactDisplay from "./NavContactDisplay.jsx";
import styles from '../style/navbar.module.css'

function NavBar(){

    const {selectedContactDetails} = useSelectedContactContext();

    return (
        <nav className={styles.container}>
            <div>
                {selectedContactDetails.id !== 0 && <NavContactDisplay />}
            </div>
            <div className={styles.navIcons}>
                <ul className={styles.mainIconsUl}>
                    <li className={styles.mainIconsLi}>
                        <button className={styles.navMenus} type="button"
                                disabled={selectedContactDetails.id === 0 || selectedContactDetails.id === null}
                                data-bs-toggle="modal"
                                data-bs-target={selectedContactDetails.type==="GROUP" ? "#editGroupModal" : "#editUserModal"}>
                            <i className="bi bi-person-fill-gear"></i>
                        </button>
                    </li>
                    <li className={styles.mainIconsLi}>
                        <button className={styles.navMenus} type="button" data-bs-toggle="modal" data-bs-target="#addGroupModal">
                            <i className="bi bi-people-fill"></i>
                        </button>
                    </li>
                    <li className={styles.mainIconsLi}>
                        <button className={styles.navMenus} type="button"><i className="bi bi-list"></i></button>
                        <NavbarSubmenuComp />
                    </li>
                </ul>
            </div>
        </nav>
    )
}

export default NavBar;

