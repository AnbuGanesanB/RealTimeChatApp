import {useSelectedContactContext} from "../context/SelectedContactContext.jsx";
import NavbarSubmenuComp from "./NavbarSubmenuComp.jsx";
import NavContactDisplay from "./NavContactDisplay.jsx";
import styles from '../style/navbar.module.css'

function NavBar(props){

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
                                title={selectedContactDetails.type==="GROUP" ? "Edit Group" : "Edit User Contact"}
                                onClick={selectedContactDetails.type==="GROUP" ? props.onEditGroupContact : props.onEditUserContact}>
                            <i className="bi bi-person-fill-gear"></i>
                        </button>
                    </li>
                    <li className={styles.mainIconsLi}>
                        <button className={styles.navMenus} type="button" title="Add Group" onClick={props.onAddGroup}>
                            <i className="bi bi-people-fill"></i>
                        </button>
                    </li>
                    <li className={styles.mainIconsLi}>
                        <button className={styles.navMenus} type="button" title="Settings"><i className="bi bi-list"></i></button>
                        <NavbarSubmenuComp />
                    </li>
                </ul>
            </div>
        </nav>
    )
}

export default NavBar;

