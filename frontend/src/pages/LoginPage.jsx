import SignUpComponent from './../components/SignUpComponent';
import SignInComponent from './../components/SignInComponent';
import { useNavigate } from "react-router-dom";
import './../style/LoginPage.css'
import { useUserContext } from './../context/UserContext.jsx';
import {fetchUser, notifyProfileUpdate} from './../service/service.js'
import { BASE_URL } from '../config';
import {useEffect, useState} from "react";
import { Modal } from "bootstrap"

function LoginPage() {

    const { user, setUser } = useUserContext();
    const navigate = useNavigate();
    const [signUpError, setSignUpError] = useState('');
    const [signInError, setSignInError] = useState(null);

    useEffect(() => {
        if (user?.userId !== 0) {
            console.log("You are already logged in");
            navigate("/home");
        }
    }, [user?.userId, navigate]);

    const handleSignInSubmit = async (event) => {
        event.preventDefault();

        const formData = new FormData(event.target);
        const credentials = Object.fromEntries(formData.entries());

        try {
            const response = await fetch(`${BASE_URL}/login`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(credentials),
            });

            if (!response.ok) {
                const errorResponse = await response.json();
                throw new Error(errorResponse.message);
            }

            const result = await response.json();
            console.log("Login Successful:", result);

            event.target.reset();
            //setRecipientId(0);

            if(result?.token){
                localStorage.setItem("token", result.token);

                const userInfos = await fetchUser();
                setUser({ ...userInfos, isLoggedIn: true });

                await notifyProfileUpdate(userInfos.userId);
                navigate("/home");
            }

        } catch (error) {
            console.error("Error:", error.message);
            setSignInError(error.message);
        }
    };

    const handleSignUpSubmit = async (event) => {
        event.preventDefault();


        const formData = new FormData(event.target);
        const newUserDetails = Object.fromEntries(formData.entries());

        try {
            const response = await fetch(`${BASE_URL}/register`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(newUserDetails),
            });

            if (!response.ok) {
                const errorData = await response.json();

                if ('error' in errorData) {
                    throw new Error(errorData.error);
                } else {
                    const allMessages = Object.values(errorData).join('\n');
                    throw new Error(allMessages);
                }
            }

            setSignUpError('');
            console.log("Registration Successful:");
            event.target.reset();

        } catch (error) {
            setSignUpError(error.message);
            console.error("Error:", error.message);
            //alert("Registration failed! Please try again.");

        } finally {
            const modalEl = document.getElementById('SignUpConfirmModal');
            if (modalEl) {
                const modalInstance = Modal.getOrCreateInstance(modalEl);
                modalInstance.show();
            }
        }
    };

    return(
        <div className="login-screen">
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
                        <SignInComponent
                            handleSignInSubmit={handleSignInSubmit}
                            signInError={signInError} />
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
                            signUpError={signUpError}
                        />
                    </div>
                </div>
            </div>
        </div>)
}

export default LoginPage;