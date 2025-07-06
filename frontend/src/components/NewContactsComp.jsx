import {useEffect, useState} from "react";
import NewContactComp from "./NewContactComp.jsx";
import {useUserContext} from "../context/UserContext.jsx";
import styles from "../style/ChatSelection.module.css"
import {BASE_URL} from '../config';

function NewContactsComp() {

    const [query, setQuery] = useState("");
    const [newContacts, setNewContacts] = useState([]);
    const [loading, setLoading] = useState(false);

    const {user} = useUserContext();
    let debounceTimer;

    const handleSearch = (e) => {
        const value = e.target.value;
        console.log("query: "+value);
        setQuery(value);

        clearTimeout(debounceTimer);

        debounceTimer = setTimeout(async () => {
            await fetchResults(value);
        }, 2000);
    };

    const fetchResults = async (searchQuery) => {
        if (!searchQuery) {
            setNewContacts([]);
            return;
        }

        setLoading(true);

        try {
            const token = localStorage.getItem("token");
            const response = await fetch(
                `${BASE_URL}/search/newContacts?searchTerm=${searchQuery}&userId=${user.userId}`,{
                    method: "GET",
                    headers: { Authorization: `Bearer ${token}` },
                });
            const newContactsData = await response.json();
            setNewContacts(newContactsData);
        } catch (error) {
            console.error("Error fetching data:", error);
        }

        setLoading(false);
    };

    const clearSearch = () => {
        setQuery("");
        setNewContacts([]);
    }

    return(
        <>
            <input type="text"
                   className={styles.newContactSearch}
                   value={query}
                   placeholder="Search Users..."
                   onChange={(e) => {handleSearch(e)}} />
            {query && (
                <button
                    className={styles.searchClear}
                    onClick={clearSearch}
                >
                    ✖
                </button>
            )}

            {/* Loading Indicator */}
            {loading && <p>Loading...</p>}

            {newContacts.map(contact => (<NewContactComp key={contact.id} contact={contact} />))}

        </>
    )
}

export default NewContactsComp;