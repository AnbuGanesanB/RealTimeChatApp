import {useState} from "react";
import {sendNewContactRequest} from "../service/service.js";
import {useUserContext} from "../context/UserContext.jsx";
import styles from "../style/contact.module.css";
import AvatarComp from "./AvatarComp.jsx";

function NewContactComp({contact}) {
    const [isAdded, setIsAdded] = useState(false);
    const {user} = useUserContext()
    console.log(contact)

    const handleAddContact = () => {
        console.log("clicked ID: "+contact.id);
        setIsAdded(true);                                 // Disable the button after clicking
        sendNewContactRequest(user.userId, contact.id);
    };

    return (
        <div className={styles.contactCard}>
            <AvatarComp contact={contact} isStatusNeeded={true} styles={styles} />
            <div className={styles.content}>
                <div>{contact.name}</div>
                <div>{contact.emailId}</div>
            </div>
            <button type="button"
                    className={styles.newContactAdd}
                    onClick={() => {handleAddContact()}}
                    disabled={isAdded} title="Add contact"
                    >
                <span>{isAdded ? "✔" : "+"}</span>
            </button>
        </div>
    )
}

export default NewContactComp;