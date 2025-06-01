import SignUpComponent from './../components/SignUpComponent';
import SignInComponent from './../components/SignInComponent';
import { useNavigate } from "react-router-dom";
import './../style/LoginPage.css'
import { useUserContext } from './../context/UserContext.jsx';
import {useRecipientContext} from "../context/RecipientContext.jsx";
import { notifyProfileUpdate } from './../service/service.js'

function LoginPage() {

    const {setRecipientId} = useRecipientContext();
    const { setUser } = useUserContext();
    const navigate = useNavigate();

    const handleSignInSubmit = async (event) => {
        event.preventDefault();

        const formData = new FormData(event.target);
        const credentials = Object.fromEntries(formData.entries());

        try {
            const response = await fetch("http://localhost:8080/signin", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(credentials),
            });

            if (!response.ok) {
                throw new Error("Login failed! Please check your credentials.");
            }

            const result = await response.json();
            console.log("Login Successful:", result);

            event.target.reset();
            setRecipientId(0);

            const loggedUser = {
                userId: result.userId,
                name: result.name,
                emailId: result.emailId,
                aboutMe: result.aboutMe,
                dpAvailable: result.dpAvailable,
                dpPath: result.dpPath,
                initials: result.initials,
                onlineStatus: result.onlineStatus,
                isLoggedIn: true
            };
            setUser(loggedUser);
            notifyProfileUpdate(result.userId);

            navigate("/home");

        } catch (error) {
            console.error("Error:", error.message);
            alert("Login failed! Please try again.");
        }
    };

    const handleSignUpSubmit = async (event) => {
        event.preventDefault();

        const formData = new FormData(event.target);
        const newUserDetails = Object.fromEntries(formData.entries());

        try {
            const response = await fetch("http://localhost:8080/register", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(newUserDetails),
            });

            if (!response.ok) {
                throw new Error("Registration failed. Please try again!!.");
            }

            //const result = await response.status.json();
            console.log("Registration Successful:");

            // Clear form fields after success
            event.target.reset();
        } catch (error) {
            console.error("Error:", error.message);
            alert("Registration failed! Please try again.");
        }
    };

    return(
        <>
            <div className="login-page-card">
                <nav>
                    <div className="nav nav-tabs d-flex w-100" id="nav-tab" role="tablist">
                        <button
                            className="nav-link active flex-grow-1"
                            id="nav-login-tab"
                            data-bs-toggle="tab"
                            data-bs-target="#nav-login"
                            type="button"
                            role="tab"
                            aria-controls="nav-login"
                            aria-selected="true">SignIn</button>
                        <button
                            className="nav-link flex-grow-1"
                            id="nav-register-tab"
                            data-bs-toggle="tab"
                            data-bs-target="#nav-register"
                            type="button"
                            role="tab"
                            aria-controls="nav-register"
                            aria-selected="false">Register</button>
                    </div>
                </nav>
                <div className="tab-content" id="nav-tabContent">
                    <div
                        className="tab-pane fade show active"
                        id="nav-login"
                        role="tabpanel"
                        aria-labelledby="nav-login-tab"
                    >
                        <h3>Sign-In Form</h3>
                        <SignInComponent handleSignInSubmit={handleSignInSubmit} />
                    </div>

                    <div
                        className="tab-pane fade"
                        id="nav-register"
                        role="tabpanel"
                        aria-labelledby="nav-register-tab"
                    >
                        <h3>Sign-Up Form</h3>
                        <SignUpComponent
                            handleSignUpSubmit={handleSignUpSubmit}
                        />
                    </div>
                </div>
            </div>
        </>)
}

export default LoginPage;