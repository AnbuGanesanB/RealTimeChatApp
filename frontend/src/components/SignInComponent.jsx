function SignInComponent({ handleSignInSubmit }){

    return (
        <form onSubmit={(event) => handleSignInSubmit(event)}>
            <div className="mb-3">
                <label htmlFor="loginEmail" className="form-label">
                    Email address
                </label>
                <input
                    type="text"
                    name="emailId"
                    className="form-control"
                    id="loginEmail"
                    required
                />
            </div>
            <div className="mb-3">
                <label htmlFor="loginPassword" className="form-label">
                    Password
                </label>
                <input
                    type="password"
                    className="form-control"
                    name="password"
                    id="loginPassword"
                    required
                />
            </div>
            <button type="submit" className="btn btn-primary">
                SignIn
            </button>
        </form>
    );
}

export default SignInComponent;