let favoriteCourses = [];

async function loadFavorites() {
    const response = await fetch("/favorites");
    favoriteCourses = await response.json();
}

async function loadFilters() {
    const response = await fetch("/api/filters");
    const data = await response.json();

    populateDropdown("deptFilter", data.departments);
    populateDropdown("profFilter", data.professors);
    populateDropdown("creditsFilter", data.credits);
}

function timeInputToMinutes(value) {
    if (!value) return "";
    const [h, m] = value.split(":").map(Number);
    return h * 60 + m;
}

function formatTime(minutes) {
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;

    const hour = h % 12 === 0 ? 12 : h % 12;
    const ampm = h < 12 ? "AM" : "PM";

    return `${hour}:${m.toString().padStart(2, "0")} ${ampm}`;
}

function minutesToTimeInput(minutes) {
    if (minutes === "" || minutes == null) return "";

    const total = Number(minutes);
    const h = Math.floor(total / 60);
    const m = total % 60;

    return `${h.toString().padStart(2, "0")}:${m.toString().padStart(2, "0")}`;
}

function populateDropdown(id, values) {
    const select = document.getElementById(id);

    values.sort();

    values.forEach(v => {
        const option = document.createElement("option");
        option.value = v;
        option.textContent = v;
        select.appendChild(option);
    });
}

function applyFiltersFromUrl() {
    const params = new URLSearchParams(window.location.search);

    const query = params.get("q") || "";
    const dept = params.get("dept") || "";
    const prof = params.get("prof") || "";
    const credits = params.get("credits") || "";
    const startTime = params.get("start") || "";
    const endTime = params.get("end") || "";
    const daysParam = params.get("days") || "";
    const required = params.get("required") === "true";
    const elective = params.get("elective") === "true";

    document.getElementById("searchInput").value = query;
    document.getElementById("deptFilter").value = dept;
    document.getElementById("profFilter").value = prof;
    document.getElementById("creditsFilter").value = credits;
    document.getElementById("startTimeFilter").value = minutesToTimeInput(startTime);
    document.getElementById("endTimeFilter").value = minutesToTimeInput(endTime);

    const requiredFilter = document.getElementById("requiredFilter");
    const electiveFilter = document.getElementById("electiveFilter");

    if (requiredFilter) {
        requiredFilter.checked = required;
    }

    if (electiveFilter) {
        electiveFilter.checked = elective;
    }

    document.querySelectorAll(".dayFilter").forEach(cb => {
        cb.checked = daysParam.includes(cb.value);
    });
}

function validateTimeRange() {
    const startInput = document.getElementById("startTimeFilter");
    const endInput = document.getElementById("endTimeFilter");
    const msg = document.getElementById("timeValidationMessage");

    const startMinutes = timeInputToMinutes(startInput.value);
    const endMinutes = timeInputToMinutes(endInput.value);

    if (startMinutes === "" || endMinutes === "") {
        msg.classList.add("d-none");
        return true;
    }

    if (startMinutes > endMinutes) {
        msg.classList.remove("d-none");
        return false;
    }

    msg.classList.add("d-none");
    return true;
}

async function searchCourses() {
    if (!validateTimeRange()) {
        return;
    }

    const query = document.getElementById("searchInput").value;
    const semester = sessionStorage.getItem("selectedSemester");
    const major = sessionStorage.getItem("selectedMajor");

    const dept = document.getElementById("deptFilter").value;
    const prof = document.getElementById("profFilter").value;
    const credits = document.getElementById("creditsFilter").value;

    const requiredFilter = document.getElementById("requiredFilter");
    const electiveFilter = document.getElementById("electiveFilter");

    const requiredOnly = requiredFilter ? requiredFilter.checked : false;
    const electiveOnly = electiveFilter ? electiveFilter.checked : false;

    const days = Array.from(document.querySelectorAll(".dayFilter:checked"))
        .map(cb => cb.value);

    const startTime = timeInputToMinutes(
        document.getElementById("startTimeFilter").value
    );

    const endTime = timeInputToMinutes(
        document.getElementById("endTimeFilter").value
    );

    const params = new URLSearchParams({
        q: query,
        dept: dept,
        prof: prof,
        credits: credits,
        semester: semester || "",
        major: major || "",
        required: requiredOnly,
        elective: electiveOnly,
        days: days.join(""),
        start: startTime,
        end: endTime
    });

    const response = await fetch(`/api/search?${params}`);
    const courses = await response.json();

    displayResults(courses);
}

function displayResults(courses) {
    const resultsDiv = document.getElementById("results");
    resultsDiv.innerHTML = "";

    if (courses.length === 0) {
        resultsDiv.innerHTML = `
            <div class="alert alert-secondary mb-0">
                No courses found
            </div>
        `;
        return;
    }

    courses.forEach(course => {
        const card = document.createElement("div");
        card.className = "card shadow-sm";

        const cardBody = document.createElement("div");
        cardBody.className = "card-body";

        const heartBtn = document.createElement("button");
        heartBtn.classList.add("favorite-heart-btn");
        heartBtn.innerHTML = "♥";
        heartBtn.style.float = "right";

        const isFavorite = favoriteCourses.some(f =>
            f.name === course.name &&
            f.section === course.section
        );

        if (isFavorite) {
            heartBtn.classList.add("favorited");
        }

        heartBtn.addEventListener("click", function (e) {
            e.preventDefault();
            e.stopPropagation();

            const isFavoriteNow = heartBtn.classList.contains("favorited");

            fetch("/favorites", {
                method: isFavoriteNow ? "DELETE" : "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(
                    isFavoriteNow
                        ? { name: course.name, section: course.section }
                        : course
                )
            })
                .then(response => response.text())
                .then(() => {
                    heartBtn.classList.toggle("favorited");
                })
                .catch(error => {
                    console.error("Error toggling favorite:", error);
                });
        });

        const addButton = document.createElement("button");
        addButton.className = "btn btn-success mt-3";
        addButton.textContent = "Add course";
        addButton.onclick = () => addCourse(addButton, course);

        cardBody.innerHTML = `
            <h5 class="card-title mb-3">${course.courseCode} ${course.section}</h5>
            <h6 class="card-subtitle mb-3 text-muted">${course.name}</h6>

            <p class="mb-1 d-none"><strong>Department:</strong> ${course.department}</p>
            <p class="mb-1 d-none"><strong>Professors:</strong> ${course.professors.join(", ")}</p>
            <p class="mb-1 d-none"><strong>Semester:</strong> ${course.semester}</p>
            <p class="mb-1 d-none"><strong>Location:</strong> ${course.location}</p>
            <p class="mb-1 d-none"><strong>Credits:</strong> ${course.credits}</p>
            <p class="mb-0 d-none"><strong>Times:</strong> ${
                course.times.map(t =>
                    `${t.day} ${formatTime(t.startTime)}-${formatTime(t.endTime)}`
                ).join(", ")
            }</p>
        `;

        const showButton = document.createElement("button");
        showButton.className = "btn btn-outline-primary mt-3";
        showButton.textContent = "Show more";
        showButton.onclick = () => {
            const details = cardBody.querySelectorAll(".mb-1, .mb-0");

            if (showButton.textContent === "Show more") {
                details.forEach(item => item.classList.remove("d-none"));
                showButton.textContent = "Show less";
            } else {
                details.forEach(item => item.classList.add("d-none"));
                showButton.textContent = "Show more";
            }
        };

        cardBody.prepend(heartBtn);
        cardBody.appendChild(showButton);
        cardBody.appendChild(addButton);
        card.appendChild(cardBody);
        resultsDiv.appendChild(card);
    });
}

async function addCourse(button, course) {
    try {
        const response = await fetch("/schedule/courses", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(course)
        });

        if (response.ok) {
            button.textContent = "Added ✓";
            button.classList.remove("btn-success");
            button.classList.add("btn-secondary");
            button.disabled = true;
        } else {
            const errorMsg = await response.text();
            alert("Could not add course:\n" + errorMsg);
        }
    } catch (error) {
        console.error("Network error:", error);
        alert("Server is unreachable.");
    }
}

document.querySelectorAll("select")
    .forEach(s => s.addEventListener("change", searchCourses));

document.querySelectorAll("input[type='time']")
    .forEach(i => i.addEventListener("change", searchCourses));

document.querySelectorAll(".dayFilter")
    .forEach(cb => cb.addEventListener("change", searchCourses));

document.querySelectorAll(".majorRequirementFilter")
    .forEach(cb => cb.addEventListener("change", searchCourses));

document
    .getElementById("clearStartTimeBtn")
    .addEventListener("click", () => {
        document.getElementById("startTimeFilter").value = "";
        document.getElementById("timeValidationMessage").classList.add("d-none");
        searchCourses();
    });

document
    .getElementById("clearEndTimeBtn")
    .addEventListener("click", () => {
        document.getElementById("endTimeFilter").value = "";
        document.getElementById("timeValidationMessage").classList.add("d-none");
        searchCourses();
    });

document
    .getElementById("clearFiltersBtn")
    .addEventListener("click", () => {
        document.getElementById("startTimeFilter").value = "";
        document.getElementById("endTimeFilter").value = "";

        const timeMsg = document.getElementById("timeValidationMessage");
        if (timeMsg) {
            timeMsg.classList.add("d-none");
        }

        document.getElementById("deptFilter").value = "";
        document.getElementById("profFilter").value = "";
        document.getElementById("creditsFilter").value = "";

        const requiredFilter = document.getElementById("requiredFilter");
        const electiveFilter = document.getElementById("electiveFilter");

        if (requiredFilter) {
            requiredFilter.checked = false;
        }

        if (electiveFilter) {
            electiveFilter.checked = false;
        }

        document
            .querySelectorAll(".dayFilter")
            .forEach(cb => cb.checked = false);

        searchCourses();
    });

async function initPage() {
    await loadFilters();
    applyFiltersFromUrl();
    await loadFavorites();
    await searchCourses();
}

function roundDownToInterval(minutes, interval) {
    return Math.floor(minutes / interval) * interval;
}

function roundUpToInterval(minutes, interval) {
    return Math.ceil(minutes / interval) * interval;
}

function buildTimeOptions(start, end, interval) {
    const values = [];

    for (let t = start; t <= end; t += interval) {
        values.push(t);
    }

    return values;
}

initPage();