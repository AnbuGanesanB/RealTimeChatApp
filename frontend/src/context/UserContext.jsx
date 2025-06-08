import { createContext, useContext, useState, useEffect } from 'react';
import { Client } from '@stomp/stompjs';
import {useSelectedContactContext} from "./SelectedContactContext.jsx";
import {useContactsContext} from "./ContactsContext.jsx";
import {fetchUser} from "../service/service.js";

const UserContext = createContext();

export const UserProvider = ({ children }) => {
    const resetUser = {
        userId: 0,
        isLoggedIn: false
    };

    const statusColors = {
        ONLINE: '#28a745',
        DO_NOT_DISTURB: '#dd0f0f',
        AWAY: '#ffc107',
        OFFLINE: '#9e9e9e',
    };

    const [user, setUser] = useState(resetUser);
    const [loading, setLoading] = useState(true);
    //const [tokenAvailable, setTokenAvailable] = useState(false);

    useEffect(() => {
        const initializeUser = async () => {
            console.log("Getting token, Initializing user");
            const token = localStorage.getItem("token");

            if (token) {
                try {
                    console.log("Fetching user...");
                    const userInfos = await fetchUser();
                    console.log("Res::",userInfos);
                    setUser({ ...userInfos, isLoggedIn: true });
                } catch (err) {
                    console.error("Error fetching user:", err);
                    localStorage.removeItem("token");
                    setUser(resetUser);
                }
            }
            setLoading(false);
        };

        initializeUser();
    }, []);


    return (
        <UserContext.Provider value={{ user, setUser, resetUser, statusColors}}>
            {!loading && children}
        </UserContext.Provider>
    );
};

export const useUserContext = () => useContext(UserContext);

/*useEffect(() => {
        /!*const initializeUser = async () => {
            console.log("check 1");
            //const storedUser = localStorage.getItem("token");
            const token = localStorage.getItem("token");

            if (token) {
                try {
                    //const userId = JSON.parse(storedUser).userId;
                    const response = await fetchUser();
                    /!*const response = await refresh(userId);*!/
                    if (response.status === 200) {
                        const updatedUser = await response.json();
                        console.log("UpdatedUser:",updatedUser);
                        setUser({ ...updatedUser, isLoggedIn: true });
                    } else {
                        localStorage.removeItem("token");
                        //setUser(resetUser);
                    }
                } catch (error) {
                    console.error("Failed to Fetch user:", error);
                    localStorage.removeItem("token");
                    //setUser(resetUser);
                }
            } /!*else {
                setUser(resetUser);
            }*!/
            setLoading(false);
        };
        initializeUser();*!/

        //if (user.userId) setLoading(false);
    }, [user.userId]);*/