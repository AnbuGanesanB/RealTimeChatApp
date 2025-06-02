import {useEffect} from "react";
import {Modal} from "bootstrap";

export const useBootstrapModalClose = (modalId, onClose) => {
    useEffect(() => {
        const modalEl = document.getElementById(modalId);
        if (!modalEl) return;

        const modal = Modal.getOrCreateInstance(modalEl);
        modal.show();

        modalEl.addEventListener('hidden.bs.modal', onClose);

        return () => {
            modalEl.removeEventListener('hidden.bs.modal', onClose);
            modal.dispose();
        };
    }, []);
};

export const modalHide = (modalId) => {
    const modalEl = document.getElementById(modalId);
    const modalInstance = Modal.getOrCreateInstance(modalEl);
    modalInstance.hide();
}