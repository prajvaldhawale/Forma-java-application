document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll("[data-autosubmit]").forEach((control) => {
        control.addEventListener("change", () => control.form.requestSubmit());
    });

    document.querySelectorAll(".quantity-button").forEach((button) => {
        button.addEventListener("click", () => {
            const form = button.closest("form");
            const input = form.querySelector("[data-quantity]");
            const step = Number(button.dataset.step);
            input.value = Math.min(99, Math.max(1, Number(input.value) + step));
            form.requestSubmit();
        });
    });

    if (window.lucide) {
        window.lucide.createIcons();
    }
});