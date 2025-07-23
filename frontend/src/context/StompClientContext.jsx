import {createContext, useCallback, useContext, useEffect, useRef, useState} from "react";
import {useUserContext} from "./UserContext.jsx";
import {Client} from "@stomp/stompjs";
import {useContactsContext} from "./ContactsContext.jsx";
import { WS_URL } from '../config';
import {logout, updateGroupMemberDetails, updateGroupMemberInContactIsSelected} from "../service/utilities.js"
import {useSelectedContactContext} from "./SelectedContactContext.jsx";
import { useNavigate } from "react-router-dom";
import {useRecipientContext} from "./RecipientContext.jsx";


const getToken = () => {
    return localStorage.getItem("token");
};

const StompClientContext = createContext();

export const StompClientProvider = ({children}) => {

    const {user, resetUser} = useUserContext();
    const {setContacts, contacts, resetContacts} = useContactsContext();
    const {resetRecipientId, resetPreviousContactId} = useRecipientContext();
    const {selectedContactDetails, setSelectedContactDetails, resetSelectedContact} = useSelectedContactContext();

    const navigate = useNavigate();

    const [stompClient, setStompClient] = useState(null);
    const [receivedMessage, setReceivedMessage] = useState("");
    const [updatedContact, setUpdatedContact] = useState(null);
    const [newReceivedContact, setNewReceivedContact] = useState(null);
    const [isStompConnected, setIsStompConnected] = useState(false);

    const subscribedTopicsRef = useRef(new Map());

    const resetStomp = useCallback(() => {
        if (stompClient && stompClient.connected) {
            stompClient.deactivate();
        }
        setStompClient(null);
        setIsStompConnected(false);
        subscribedTopicsRef.current.clear();
        setUpdatedContact(null);
        console.log("STOMP state reset.");
    }, [stompClient]);

    const subscribeIfNotExists = (client, key, topic, callback) => {
        if (!client || !client.connected) {
            console.warn(`Client not ready to subscribe to ${topic}`);
            return;
        }
        if (!subscribedTopicsRef.current.has(key)) {
            console.log("Subscribing to topic: ",topic);
            const subscription = client.subscribe(topic, callback);
            subscribedTopicsRef.current.set(key,subscription);
        }
    };

    const unsubscribeIfSubscribed = (client, key, topic) => {
        if (subscribedTopicsRef.current.has(key)) {
            console.log("Un-Subscribing from topic: ",topic);
            const subscription = subscribedTopicsRef.current.get(key);
            subscription.unsubscribe();
            subscribedTopicsRef.current.delete(key);
        }
        console.log("Current subscriptions: ",subscribedTopicsRef.current);
    };

    const selectedContactRef = useRef(selectedContactDetails);

    useEffect(() => {
        selectedContactRef.current = selectedContactDetails;
    }, [selectedContactDetails]);

    /**
     * On user Login -> Attempts to establish Websocket connection and thereby subscribing to topics
     * On logout -> deactivates the WS connection
     */
    useEffect(() => {
        if (!user?.isLoggedIn) return;

        console.log("Contacts at WebSocket subscription time:", contacts);
        const savedToken = localStorage.getItem("token");

        const client = new Client({
            brokerURL: `${WS_URL}/ws?token=${savedToken}`,
            connectHeaders: {},
            forceBinaryWSFrames: true,
            appendMissingNULLonIncoming: true,
            onConnect: () => {
                console.log('Connected to WebSocket server');
                subscribedTopicsRef.current.clear();
                setStompClient(client);
                setIsStompConnected(true);

                subscribeIfNotExists(client,
                    "user-messages",
                    `/user/${user.userId}/queue/messages`,
                    (message) => {
                        console.log("In Receiving-message Block");
                        const newMessage = JSON.parse(message.body);
                        console.log(`Message for ${user.name}:`, newMessage);
                        setReceivedMessage(newMessage);
                    }
                );

                subscribeIfNotExists(client,
                    "newContact",
                    `/user/${user.userId}/queue/newContact`,
                    (contactMessage) => {
                        console.log("In Receiving-contact Block");
                        const newContact = JSON.parse(contactMessage.body);
                        console.log(`New Contact for ${user.name}:`, newContact);
                        setNewReceivedContact(newContact);
                    }
                );

                subscribeIfNotExists(client,
                    "user-updates",
                    `/user/${user.userId}/queue/updatedContact`,
                    (updateContactMessage) => {
                        console.log("In Receiving-updatedContact Block");
                        const newUpdatedContact = JSON.parse(updateContactMessage.body);
                        console.log(`Updated Contact for ${user.name}:`, newUpdatedContact);
                        setUpdatedContact(newUpdatedContact);
                    }
                );
            },
            onStompError: (error) => {
                setIsStompConnected(false);
                console.error('STOMP Error:', error);
            },
            onDisconnect: () => {
                console.log('Disconnected from WebSocket server');
            },
            onWebSocketClose: (event) => {
                resetStomp();
                console.log('WebSocket closed');

                if (event.code === 1006) {
                    console.warn('WS closed due to servers response', event);
                    logout({
                        resetUser,resetContacts,resetRecipientId,resetPreviousContactId,resetSelectedContact,resetStomp,navigate});
                }
            }

        });

        client.activate(); // Establish connection

        // Cleanup WebSocket connection on logout or page refresh
        return () => {
            console.log("Cleaning up WebSocket connection voluntarily...");
            resetStomp();
        };

    }, [user.isLoggedIn, user.userId]);

    useEffect(() => {
        console.log(`StompClient now is: ${stompClient}`);
        if (!stompClient || !isStompConnected || !stompClient.connected) return;

        contacts
            .filter((contact) => contact.type === "GROUP")
            .filter((contact) => !contact.removedMemberIds.includes(user.userId))
            .forEach((contact) => {
                const groupId = contact.contactPersonOrGroupId;

                subscribeIfNotExists(stompClient,
                    `group-${groupId}-messages`,
                    `/group/${groupId}/queue/messages`,
                    (message) => {
                        const newMessage = JSON.parse(message.body);
                        console.log(`Message came for Group:`, newMessage);
                        setReceivedMessage(newMessage);
                    }
                );

                subscribeIfNotExists(stompClient,
                    `group-${groupId}-updates`,
                    `/group/${groupId}/queue/updates`,
                    (update) => {
                        const newUpdatedMemberInfo = JSON.parse(update.body);
                        updateGroupMember(groupId,newUpdatedMemberInfo, selectedContactRef.current);
                    }
                );
            });

        contacts
            .filter((contact) => contact.type === "GROUP")
            .filter((contact) => contact.removedMemberIds.includes(user.userId))
            .forEach((contact) => {
                const groupId = contact.contactPersonOrGroupId;
                unsubscribeIfSubscribed(stompClient,
                    `group-${groupId}-messages`,
                    `/group/${groupId}/queue/messages`);

                unsubscribeIfSubscribed(stompClient,
                    `group-${groupId}-updates`,
                    `/group/${groupId}/queue/updates`);

            });
    },[contacts, stompClient, isStompConnected]);

    const updateGroupMember = (groupId, updatedMemberInfo, currentSelectedContactDetails) => {

        setContacts(prevContacts => updateGroupMemberDetails(prevContacts, groupId, updatedMemberInfo));

        if(groupId === currentSelectedContactDetails.contactPersonOrGroupId && currentSelectedContactDetails.type === "GROUP") {
            setSelectedContactDetails(prevDetails => updateGroupMemberInContactIsSelected(prevDetails,updatedMemberInfo));
        }
    }

    return (
        <StompClientContext.Provider value={{stompClient, receivedMessage, updatedContact, newReceivedContact, resetStomp}}>
            {children}
        </StompClientContext.Provider>
    );

}

export const useStompClientContext = () => useContext(StompClientContext);