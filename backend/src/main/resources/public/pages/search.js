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

    function populateTimeDropdown(id, values) {
        const select = document.getElementById(id);

        values.forEach(v => {
            const option = document.createElement("option");
            option.value = v;
            option.textContent = formatTime(v);
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

        document.getElementById("searchInput").value = query;
        document.getElementById("deptFilter").value = dept;
        document.getElementById("profFilter").value = prof;
        document.getElementById("creditsFilter").value = credits;
        document.getElementById("startTimeFilter").value = formatTime(startTime);
        document.getElementById("endTimeFilter").value = formatTime(endTime);
        ``

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

      // Valid if either is empty
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
            return; // stop search if invalid
        }

        const query = document.getElementById("searchInput").value;
        const semester = sessionStorage.getItem("selectedSemester");

        const dept = document.getElementById("deptFilter").value;
        const prof = document.getElementById("profFilter").value;
        const credits = document.getElementById("creditsFilter").value;

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
            semester: semester,
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

            const addButton = document.createElement("button");
            addButton.className = "btn btn-success mt-3";
            addButton.textContent = "Add course";
            addButton.onclick = () => addCourse(addButton, course);

            cardBody.innerHTML = `
                <h5 class="card-title mb-3">${course.courseCode} - ${course.name}</h5>
                <h6 class="card-subtitle mb-3 text-muted">Section ${course.section}</h6>

                <p class="mb-1"><strong>Department:</strong> ${course.department}</p>
                <p class="mb-1"><strong>Professors:</strong> ${course.professors.join(", ")}</p>
                <p class="mb-1"><strong>Semester:</strong> ${course.semester}</p>
                <p class="mb-1"><strong>Location:</strong> ${course.location}</p>
                <p class="mb-1"><strong>Credits:</strong> ${course.credits}</p>
                <p class="mb-0"><strong>Times:</strong> ${
                    course.times.map(t =>
                        `${t.day} ${formatTime(t.startTime)}-${formatTime(t.endTime)}`
                    ).join(", ")
                }</p>
            `;

            cardBody.appendChild(addButton);
            card.appendChild(cardBody);
            resultsDiv.appendChild(card);
        });
    }

    async function addCourse(button, course) {
        try {
            const response = await fetch("/api/add-course", {
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

    async function initPage() {
        await loadFilters();
        applyFiltersFromUrl();
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