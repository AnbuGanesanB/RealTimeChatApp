import SignUpConfirmModal from "../modals/SignUpConfirmModal.jsx";

function SignUpComponent({ handleSignUpSubmit }){
    return (
        <>
            <form onSubmit={(event) => handleSignUpSubmit(event)}>
                <div className="mb-3">
                    <label htmlFor="registerName" className="form-label">
                        Name
                    </label>
                    <input
                        type="text"
                        className="form-control"
                        name="register_name"
                        id="registerName"
                        required
                    />
                </div>
                <div className="mb-3">
                    <label htmlFor="registerEmail" className="form-label">
                        Email address
                    </label>
                    <input
                        type="text"
                        className="form-control"
                        name="register_emailId"
                        id="registerEmail"
                        required
                    />
                </div>
                <div className="mb-3">
                    <label htmlFor="registerPassword" className="form-label">
                        Password
                    </label>
                    <input
                        type="password"
                        className="form-control"
                        name="register_password"
                        id="registerPassword"
                        required
                    />
                </div>
                <button type="submit" className="btn btn-success" data-bs-toggle="modal" data-bs-target="#SignUpConfirmModal">
                    SignUp
                </button>
            </form>
            <SignUpConfirmModal />
        </>
    );
}

export default SignUpComponent;