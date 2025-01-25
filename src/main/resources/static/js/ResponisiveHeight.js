    function adjustHeight() {
        const cardBody = document.getElementById("cardBody");
        const viewportHeight = window.innerHeight;
        cardBody.style.maxHeight = `${viewportHeight * 0.95}px`; // Adjust 80% of the viewport height
    }

    window.addEventListener("resize", adjustHeight);
    document.addEventListener("DOMContentLoaded", adjustHeight);
 