import { createPortal } from "react-dom";

function SignUpConfirmModal({ errorMessage }) {
    return createPortal(
        <div className="modal fade" id="SignUpConfirmModal" tabIndex="-1" aria-labelledby="addGroupModalLabel" aria-hidden="true">
            <div className="modal-dialog modal-dialog-scrollable">
                <div className="modal-content">
                    <div className="modal-header">
                        <h5 className="modal-title" id="addGroupModalLabel">SignUp Confirmation</h5>
                        <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div className="modal-body">
                        {errorMessage ? (
                            <div className="alert alert-danger" style={{ whiteSpace: 'pre-line' }}>{errorMessage}</div>
                        ) : (
                            <div className="alert alert-success">Sign up successful!</div>
                        )}
                    </div>

                </div>
            </div>
        </div>,
        document.getElementById("modal-root")
    );

}

export default SignUpConfirmModal;