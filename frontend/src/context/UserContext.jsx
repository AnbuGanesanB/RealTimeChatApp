import { createContext, useContext, useState, useEffect } from 'react';
import {fetchUser} from "../service/service.js";

const UserContext = createContext();

export const UserProvider = ({ children }) => {

    const getDefaultUser = () => ({
        userId: 0,
        isLoggedIn: false
    });

    const resetUser = () => setUser(getDefaultUser());

    const statusColors = {
        ONLINE: '#28a745',
        DO_NOT_DISTURB: '#dd0f0f',
        AWAY: '#ffc107',
        OFFLINE: '#9e9e9e',
    };

    const [user, setUser] = useState(getDefaultUser);
    const [loading, setLoading] = useState(true);

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