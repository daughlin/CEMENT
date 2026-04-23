let favoriteCourses = [];

document.addEventListener("DOMContentLoaded", function () {
    console.log("Drag and drop is working");
    setupDropzones();
    loadFavorites();
    setupDraggables();


    const toggleBtn = document.getElementById("toggleFavoritesBtn");
    const favoritesPanel = document.getElementById("favoritesPanel");
    const favoritesContent = document.getElementById("favoritesContent");

    if (toggleBtn && favoritesPanel && favoritesContent) {
        toggleBtn.addEventListener("click", function () {
            favoritesPanel.classList.toggle("collapsed");
            favoritesContent.classList.toggle("collapsed");

            const isCollapsed = favoritesContent.classList.contains("collapsed");
            toggleBtn.textContent = isCollapsed ? "Show" : "Hide";
        });
    }

});

function renderFavorites() {
    const favoritesList = document.getElementById("favoritesList");
    favoritesList.innerHTML = "";

    favoriteCourses.forEach(course => {
        favoritesList.appendChild(createFavoriteCourse(course));
    });

    setupDraggables();
}

function createFavoriteCourse(course) {
    const div = document.createElement("div");
    div.classList.add("favorite-course", "mb-2");

    div.dataset.name = course.name;
    div.dataset.section = course.section;
    div.dataset.course = JSON.stringify(course);
    div.dataset.times = JSON.stringify(course.times || []);

    const key = `${course.name}|${course.section}`;

    if (window.scheduledCourses && window.scheduledCourses.has(key)) {
        div.classList.add("in-calendar");
    } else {
        div.classList.add("draggable-course");
    }

    div.innerHTML = `
        <div class="favorite-course-header">
            <div class="favorite-course-main">
                <div class="favorite-title">
                    ${course.name} <span class="favorite-section">(${course.section})</span>
                </div>
            </div>

            <button class="favorite-heart-btn" type="button">♥</button>
        </div>

        <div class="favorite-times">
            ${formatCourseDays(course.times)} ${formatCourseTimeRange(course.times)}
        </div>
    `;

    const heartBtn = div.querySelector(".favorite-heart-btn");
    heartBtn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();

        fetch("/favorites", {
            method: "DELETE",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                name: course.name,
                section: course.section
            })
        })
        .then(response => response.text())
        .then(text => {
            console.log(text);
            loadFavorites();
        })
        .catch(error => {
            console.error("Error unfavoriting course:", error);
        });
    });

    return div;
}

function formatCourseDays(times) {
    if (!times || times.length === 0) return "";

    return times.map(t => t.day).join(" ");
}

function formatCourseTimeRange(times) {
    if (!times || times.length === 0) return "";

    return times
        .map(t => `${minutesToTime(t.startTime)} - ${minutesToTime(t.endTime)}`)
        .join(", ");
}

function minutesToTime(minutes) {
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;

    const hour12 = h % 12 === 0 ? 12 : h % 12;
    const ampm = h < 12 ? "AM" : "PM";
    const minStr = m.toString().padStart(2, "0");

    return `${hour12}:${minStr} ${ampm}`;
}

function getDraggedCourse(element) {
    return JSON.parse(element.dataset.course || "{}");
}


function highlightValidDropzones(draggedElement) {
    const times = JSON.parse(draggedElement.dataset.times || "[]");

    document.querySelectorAll(".calendar-dropzone").forEach(zone => {
        zone.classList.remove("valid-dropzone");
        zone.classList.remove("invalid-dropzone");

        const zoneDay = zone.dataset.day;
        const zoneStart = parseInt(zone.dataset.start, 10);

        const isValid = times.some(time =>
            time.day === zoneDay && time.startTime === zoneStart
        );

        if (isValid) {
            zone.classList.add("valid-dropzone");
        } else {
            zone.classList.add("invalid-dropzone");
        }
    });
}

function clearDropzoneHighlights() {
    document.querySelectorAll(".calendar-dropzone").forEach(zone => {
        zone.classList.remove("valid-dropzone");
        zone.classList.remove("invalid-dropzone");
    });
}

function isValidDrop(draggedElement, zone) {
    const times = JSON.parse(draggedElement.dataset.times || "[]");
    const zoneDay = zone.dataset.day;
    const zoneStart = parseInt(zone.dataset.start, 10);

    return times.some(time =>
        time.day === zoneDay && time.startTime === zoneStart
    );
}

function loadFavorites() {
    return fetch("/favorites")
        .then(response => response.json())
        .then(courses => {
            console.log("favorites from backend:", courses);
            favoriteCourses = courses;
            window.favoriteCourses = courses;
            renderFavorites()
        })
        .catch(error => {
            console.error("Error loading favorites:", error);
        });
}

function setupDraggables() {
    interact(".draggable-course").draggable({
        listeners: {
            start(event) {
                const target = event.target;
                const rect = target.getBoundingClientRect();

                target.classList.add("dragging");

                target.style.width = `${rect.width}px`;
                target.style.height = `${rect.height}px`;
                target.style.position = "fixed";
                target.style.left = `${rect.left}px`;
                target.style.top = `${rect.top}px`;
                target.style.margin = "0";
                target.style.zIndex = "9999";

                target.setAttribute("data-x", 0);
                target.setAttribute("data-y", 0);

                highlightValidDropzones(target);
            },

            move(event) {
                const target = event.target;

                const x = (parseFloat(target.getAttribute("data-x")) || 0) + event.dx;
                const y = (parseFloat(target.getAttribute("data-y")) || 0) + event.dy;

                target.style.transform = `translate(${x}px, ${y}px)`;
                target.setAttribute("data-x", x);
                target.setAttribute("data-y", y);
            },

            end(event) {
                const target = event.target;

                targetReset(target);
                target.classList.remove("dragging");

                target.style.position = "";
                target.style.left = "";
                target.style.top = "";
                target.style.width = "";
                target.style.height = "";
                target.style.margin = "";
                target.style.zIndex = "";

                clearDropzoneHighlights();
            }
        }
    });
}


function setupDropzones() {
    interact("#favoritesList").dropzone({
        accept: ".draggable-course",
        overlap: 0.5,

        ondragenter(event) {
            event.target.classList.add("drop-active");
        },

        ondragleave(event) {
            event.target.classList.remove("drop-active");
        },

        ondrop(event) {

            console.log("favorites drop fired");

            event.target.classList.remove("drop-active");

            const dragged = event.relatedTarget;
            const name = dragged.dataset.name;
            const section = dragged.dataset.section;

            console.log("Dropped into favorites:", name, section);

            const course = getDraggedCourse(dragged);

            fetch("/favorites", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(course)
            })
            .then(response => response.text())
            .then(text => {
                console.log(text);

                return fetch("/schedule/courses", {
                    method: "DELETE",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ name, section })
                });
            })
            .then(response => response.text())
            .then(text => {
                console.log(text);
                location.reload();
            })
            .catch(error => {
                console.error("Error moving course to favorites:", error);
            });
        }
    });

    interact(".calendar-dropzone").dropzone({
        accept: ".draggable-course",
        overlap: 0.5,

        ondragenter(event) {
            event.target.classList.add("drop-active");
        },

        ondragleave(event) {
            event.target.classList.remove("drop-active");
        },

        ondrop(event) {
            const dragged = event.relatedTarget;
            const zone = event.target;

            console.log("dragged.dataset.times:", dragged.dataset.times);
            console.log("dragged.dataset.course:", dragged.dataset.course);
            console.log("zone day/start:", zone.dataset.day, zone.dataset.start);
            console.log("isValidDrop?", isValidDrop(dragged, zone));

            event.target.classList.remove("drop-active");

            if (!isValidDrop(dragged, zone)) {
                console.log("Invalid drop");
                targetReset(dragged);
                return;
            }

            const course = getDraggedCourse(dragged);

            console.log("Valid drop");
            console.log("Adding course to calendar:", course);

            targetReset(dragged);
            clearDropzoneHighlights();

            fetch("/schedule/courses", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(course)
            })
            .then(response =>
                response.text().then(text => ({ ok: response.ok, text }))
            )
            .then(({ ok, text }) => {
                console.log("add-course response:", ok, text);
                if (!ok) {
                    throw new Error(text);
                }

                targetReset(dragged);
                location.reload();
            })
            .catch(error => {
                console.error("Error adding course to calendar:", error);
                targetReset(dragged);
            });
        }
    });
}

function targetReset(element) {
    element.style.transform = "translate(0px, 0px)";
    element.setAttribute("data-x", 0);
    element.setAttribute("data-y", 0);
}