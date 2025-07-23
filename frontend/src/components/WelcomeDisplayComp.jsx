import {useUserContext} from "../context/UserContext.jsx";
import styles from '../style/display.module.css';

function WelcomeDisplayComp() {

    const {user} = useUserContext();

    return (
        <div className={styles.welcomeContainer}>
            <h1>Hi {user.name}!</h1>
            <h2>Let's Talk!!</h2>
        </div>
    )
}

export default WelcomeDisplayComp;