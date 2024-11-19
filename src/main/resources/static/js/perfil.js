document.addEventListener("DOMContentLoaded", function () {
    const toggleButton = document.getElementById("toggle-password");
    const toggleIcon = document.getElementById("toggle-password-icon");
    const claveOculta = document.getElementById("clave-oculta");
    const claveVisible = document.getElementById("clave-visible");

    toggleButton.addEventListener("click", function () {
        const isHidden = claveVisible.style.display === "none";
        claveVisible.style.display = isHidden ? "inline" : "none";
        claveOculta.style.display = isHidden ? "none" : "inline";
        toggleIcon.src = isHidden ? "/images/hide.png" : "/images/show.png";
        toggleIcon.alt = isHidden ? "Ocultar" : "Revelar";
    });
});
