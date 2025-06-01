import {useUserContext} from "../context/UserContext.jsx";
import styles from '../style/display.module.css';

function WelcomeDisplayComp() {

    const {user} = useUserContext();

    return (
        <div className={styles.welcomeContainer}>
            <h2>Hi {user.name}!</h2>
            <h3>Welcome to Chat App!!</h3>
        </div>
    )
}

export default WelcomeDisplayComp;