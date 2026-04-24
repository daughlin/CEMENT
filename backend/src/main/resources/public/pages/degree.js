function openDegreePdf() {
    const major = sessionStorage.getItem("selectedMajor");

    if (!major) {
        alert("No major selected. Redirecting...");
        window.location.href = "/";
        return;
    }

    window.open(`/degrees/${encodeURIComponent(major + ".pdf")}`, "_blank");
}