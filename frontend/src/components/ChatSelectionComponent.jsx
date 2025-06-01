import NewContactsComp from "./NewContactsComp.jsx";
import { useState } from "react";
import { useContactsContext } from "../context/ContactsContext.jsx";
import { ContactComponent } from "./ChatContactComponent.jsx";
import styles from "../style/ChatSelection.module.css"

function ChatSelectionComponent() {
    const [activeTab, setActiveTab] = useState("Chats");
    const { contacts } = useContactsContext();

    const tabs = ["Chats", "Contacts", "Groups", "Users"];

    return (
        <div className={styles.chatSelectionComponent}>
            <div className={styles.userContactsPageCard}>
                <nav className={styles.customTabNav}>
                    {tabs.map((tab) => (
                        <button
                            key={tab}
                            className={`${styles.customTabButton} ${activeTab === tab ? styles.customTabButtonActive : styles.customTabButtonInactive}`}
                            onClick={() => setActiveTab(tab)}
                        >
                            {tab}
                        </button>
                    ))}
                </nav>

                <div className={styles.customTabContent}>
                    {activeTab === "Chats" && (
                        <div className={styles.customTabPane}>
                            {contacts.map((contact) => (
                                <ContactComponent key={contact.id} tab="Chats" contact={contact} />
                            ))}
                        </div>
                    )}

                    {activeTab === "Contacts" && (
                        <div className={styles.customTabPane}>
                            {contacts
                                .filter((contact) => contact.type === "USER")
                                .map((contact) => (
                                    <ContactComponent key={contact.id} tab="Contacts" contact={contact} />
                                ))}
                        </div>
                    )}

                    {activeTab === "Groups" && (
                        <div className={styles.customTabPane}>
                            {contacts
                                .filter((contact) => contact.type === "GROUP")
                                .map((contact) => (
                                    <ContactComponent key={contact.id} tab="Groups" contact={contact} />
                                ))}
                        </div>
                    )}

                    {activeTab === "Users" && (
                        <div className={styles.customTabPane}>
                            <h3>Users</h3>
                            <NewContactsComp />
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}

export default ChatSelectionComponent;
