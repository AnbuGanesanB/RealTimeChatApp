import {createContext, useContext, useEffect, useRef, useState} from "react";
import {useUserContext} from "./UserContext.jsx";
import {Client} from "@stomp/stompjs";
import {useContactsContext} from "./ContactsContext.jsx";
import { WS_URL } from '../config';

const getToken = () => {
    return localStorage.getItem("token");
};

const StompClientContext = createContext();

export const StompClientProvider = ({children}) => {

    const {user} = useUserContext();
    const {contacts, updateContactMembers} = useContactsContext();

    const [stompClient, setStompClient] = useState(null);
    const [receivedMessage, setReceivedMessage] = useState("");
    const [updatedContact, setUpdatedContact] = useState(null);
    const [newReceivedContact, setNewReceivedContact] = useState(null);
    const [groupUpdateType, setGroupUpdateType] = useState("");
    const [groupUpdate, setGroupUpdate] = useState(null);

    const subscribedTopicsRef = useRef(new Set());

    const subscribeIfNotExists = (client, key, topic, callback) => {
        if (!client || !client.connected) {
            console.warn(`Client not ready to subscribe to ${topic}`);
            return;
        }
        if (!subscribedTopicsRef.current.has(key)) {
            console.log("Subscribing to topic: ",topic);
            client.subscribe(topic, callback);
            subscribedTopicsRef.current.add(key);
        }
    };


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
                setStompClient(client);

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
                console.error('STOMP Error:', error);
            },
            onDisconnect: () => {
                console.log('Disconnected from WebSocket server');
            }
        });

        client.activate(); // Establish connection

        // Cleanup WebSocket connection on logout or page refresh
        return () => {
            client.deactivate();
            console.log("Cleaning up WebSocket connection voluntarily...");
            setStompClient(null);
            subscribedTopicsRef.current.clear();
        };

    }, [user.isLoggedIn]);

    useEffect(() => {
        console.log("In Group-useEffect block 1..");
        console.log(`StompClient now is: ${stompClient}`);
        if (!stompClient || !user?.isLoggedIn || contacts.length === 0) return;
        console.log("In Group-useEffect block 2..");

        contacts
            .filter((contact) => contact.type === "GROUP")
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
                    (groupUpdateMessage) => {
                        const groupUpdate = JSON.parse(groupUpdateMessage.body);
                        console.log(`Update came for Group:`, groupUpdate);
                        setGroupUpdate(groupUpdate);
                        setGroupUpdateType(groupUpdate.type);
                    }
                );
            });
    }, [contacts, stompClient, user?.isLoggedIn]);

    useEffect(() => {
        console.log("Entering IF 1");
        if (!groupUpdate || !groupUpdateType) return;
        if(groupUpdateType==="GROUP_MEMBER_ADD" || groupUpdateType==="GROUP_MEMBER_REMOVED"){
            console.log("Entering IF 2");
            if (groupUpdate.groupId && groupUpdate.updatedMemberDetails) {
                updateContactMembers(groupUpdate.groupId, groupUpdate.updatedMemberDetails);
            } else {
                console.warn("Group update object incomplete:", groupUpdate);
            }
            setGroupUpdate(null);
            setGroupUpdateType("");
        }
    }, [groupUpdate, groupUpdateType]);

    return (
        <StompClientContext.Provider value={{stompClient, receivedMessage, updatedContact, newReceivedContact}}>
            {children}
        </StompClientContext.Provider>
    );
}

export const useStompClientContext = () => useContext(StompClientContext);