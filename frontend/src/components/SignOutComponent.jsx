import { useNavigate } from "react-router-dom";
import {useRecipientContext} from "../context/RecipientContext.jsx";

function SignOutComponent(){

    const {setRecipientId} = useRecipientContext();
    const navigate = useNavigate();

    const handleSignOut = () => {
        localStorage.removeItem('user');
        setRecipientId(0);
        navigate("/login");
    }

    return(
        <>
            <button className="sign-out" onClick={handleSignOut}>Sign Out</button>
        </>
    )
}

export default SignOutComponent;