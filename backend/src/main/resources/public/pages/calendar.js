document.addEventListener("DOMContentLoaded", function () {

    console.log("Calendar page loaded!");

    const startOfDay = 8 * 60;   // 8:00 AM
    const endOfDay = 18 * 60;    // 6:00 PM

    // Day columns (must match HTML IDs)
    const days = { M: [], T: [], W: [], R: [], F: [] };

    const timeColumn = document.getElementById("timeColumn");
    const slotMinutes = 30;

//    for (let t = startOfDay; t < endOfDay; t += slotMinutes) {
//        const label = document.createElement("div");
//        label.classList.add("time-label");
//        label.textContent = minutesToTime(t);
//        timeColumn.appendChild(label);
//    }
window.scheduledCourses = new Set();

window.currentCourse = null;

window.refreshSchedule = function () {
    window.scheduledCourses.clear();

    const days = { M: [], T: [], W: [], R: [], F: [] };

    document.querySelectorAll(".day-grid").forEach(grid => {
        grid.innerHTML = "";
    });

    fetch("/schedule")
        .then(res => res.json())
        .then(courses => {
            courses.forEach(course => {
                const key = `${course.name}|${course.section}`;
                window.scheduledCourses.add(key);
            });

            courses.forEach(course => {
                course.times.forEach(time => {
                    if (!days[time.day]) return;

                    days[time.day].push({
                        ...course,
                        start: time.startTime,
                        end: time.endTime
                    });
                });
            });

            Object.keys(days).forEach(day => {
                const dayGrid = document.querySelector(`#${day} .day-grid`);
                if (!dayGrid) return;

                const dayCourses = days[day];
                dayCourses.sort((a, b) => a.start - b.start);

                for (let t = startOfDay; t < endOfDay; t += slotMinutes) {
                    const row = Math.floor((t - startOfDay) / slotMinutes) + 1;
                    const freeBlockEnd = getFreeBlockEnd(dayCourses, t, endOfDay);

                    const params = new URLSearchParams({
                        days: getDayGroup(day),
                        start: t,
                        end: freeBlockEnd
                    });

                    const label = document.createElement("div");
                    label.classList.add("time-label");
                    label.textContent = minutesToTime(t);
                    label.style.gridRow = `${row}`;
                    label.style.gridColumn = "1";

                    const dropCell = document.createElement("div");
                    dropCell.classList.add("calendar-dropzone");
                    dropCell.dataset.day = day;
                    dropCell.dataset.start = t;
                    dropCell.dataset.end = t + slotMinutes;
                    dropCell.style.gridRow = `${row}`;
                    dropCell.style.gridColumn = "2";

                    const link = document.createElement("a");
                    link.classList.add("add-course-link");
                    link.href = `/search?${params.toString()}`;
                    link.textContent = "+ Add";

                    dropCell.appendChild(link);

                    dayGrid.appendChild(label);
                    dayGrid.appendChild(dropCell);
                }

                dayCourses.forEach(course => {
                    addCourse(dayGrid, course);
                });
            });
        });
};

        document.querySelectorAll(".color-dot").forEach(dot => {
            dot.addEventListener("click", () => {
                if (!window.currentCourse) return;

                const color = dot.dataset.color;
                const course = window.currentCourse;

                fetch("/schedule/courses/color", {
                    method: "PATCH",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        name: course.name,
                        section: course.section,
                        color: color
                    })
                })
                .then(response => response.text())
                .then(text => {
                    console.log(text);

                    course.displayColor = color;

                    const matchingBlocks = document.querySelectorAll(".course-block");
                    matchingBlocks.forEach(block => {
                        if (
                            block.dataset.name === course.name &&
                            block.dataset.section === course.section
                        ) {
                            block.style.backgroundColor = color;
                        }
                    });

                    document.querySelectorAll(".color-dot").forEach(d => {
                        d.classList.remove("selected");
                    });
                    dot.classList.add("selected");
                });
            });
        });

        window.refreshSchedule();



    function addCourse(dayGrid, course) {

        const slotMinutes = 30;

        const startRow =
            Math.floor((course.start - startOfDay) / slotMinutes) + 1;

        const rowSpan =
            Math.ceil((course.end - course.start) / slotMinutes);

        const div = document.createElement("div");
        div.classList.add("course-block", "draggable-course");

        div.style.backgroundColor = course.displayColor || "#7A958F";

        div.dataset.name = course.name;
        div.dataset.section = course.section;

        const cleanCourse = {
            name: course.name,
            courseCode: course.courseCode,
            section: course.section,
            department: course.department,
            professors: course.professors,
            times: course.times,
            semester: course.semester,
            location: course.location,
            credits: course.credits,
            description: course.description || "",
            displayColor: course.displayColor || "#7A958F"
        };

        div.dataset.course = JSON.stringify(cleanCourse);
        div.dataset.times = JSON.stringify(course.times || []);

        div.style.gridRow = `${startRow} / span ${rowSpan}`;
        div.style.gridColumn = "2";   // only use the right column

        div.innerHTML = `
            <div class="course-title">${course.name} (${course.section})</div>
            <div class="course-time">${minutesToTime(course.start)} - ${minutesToTime(course.end)}</div>
        `;

        const detailsButton = document.createElement("button");
        detailsButton.type = "button";
        detailsButton.textContent = "Details";
        detailsButton.classList.add("btn", "btn-sm", "btn-outline-light", "mt-1", "me-1");

        detailsButton.addEventListener("click", (e) => {
            e.preventDefault();
            e.stopPropagation();

            showCourseDetails(cleanCourse);
        });

        const removeButton = document.createElement("button");
        removeButton.type = "button";
        removeButton.textContent = "Remove";
        removeButton.classList.add("btn", "btn-sm", "btn-outline-danger", "mt-1", "remove-btn");

        div.appendChild(detailsButton);

        dayGrid.appendChild(div);
    }


    function minutesToTime(minutes) {
        const h = Math.floor(minutes / 60);
        const m = minutes % 60;

        const hour12 = h % 12 === 0 ? 12 : h % 12;
        const ampm = h < 12 ? "AM" : "PM";

        const minStr = m.toString().padStart(2, "0");

        return `${hour12}:${minStr} ${ampm}`;
    }

    function getDayGroup(day) {
        if (day === "M" || day === "W" || day === "F") {
            return "MWF";
        }

        if (day === "T" || day === "R") {
            return "TR";
        }

        return day;
    }


    function showCourseDetails(course) {

        window.currentCourse = course;

        const details = document.getElementById("courseDetailsContent");
        const colorPickerSection = document.getElementById("colorPickerSection");
        if (!details) return;

        if (colorPickerSection) {
            colorPickerSection.style.display = "block";
        }

        const isFavorite = favoriteCoursesContains(course);

        details.innerHTML = `
            <div class="mb-3">
                <div class="fw-bold fs-5">${course.name}</div>
                <div class="text-muted">${course.courseCode || ""} (${course.section})</div>
            </div>

            <div class="mb-2"><strong>Department:</strong> ${course.department || ""}</div>
            <div class="mb-2"><strong>Credits:</strong> ${course.credits ?? ""}</div>
            <div class="mb-2"><strong>Semester:</strong> ${course.semester || ""}</div>
            <div class="mb-2"><strong>Location:</strong> ${course.location || "N/A"}</div>
            <div class="mb-2"><strong>Professor(s):</strong> ${(course.professors || []).join(", ") || "N/A"}</div>
            <div class="mb-3"><strong>Times:</strong><br>${formatCourseDetailsTimes(course.times)}</div>
            <div class="mb-3"><strong>Description:</strong><br>${course.description || "No description available."}</div>

            <div class="d-grid gap-2">
                <button id="detailsRemoveBtn" class="btn btn-outline-danger btn-sm">Remove from Calendar</button>
                <button id="detailsFavoriteBtn" class="btn btn-outline-secondary btn-sm">
                    ${isFavorite ? "Unfavorite" : "Favorite"}
                </button>
            </div>
        `;


        document.querySelectorAll(".color-dot").forEach(dot => {
            if (dot.dataset.color === (course.displayColor || "#7A958F")) {
                dot.classList.add("selected");
            } else {
                dot.classList.remove("selected");
            }
        });

        document.getElementById("detailsRemoveBtn")?.addEventListener("click", () => {
            fetch("/schedule/courses", {
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
                location.reload();
            });
        });

        document.getElementById("detailsFavoriteBtn")?.addEventListener("click", () => {
            const isRemoving = isFavorite;

            fetch("/favorites", {
                method: isRemoving ? "DELETE" : "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(
                    isRemoving
                        ? { name: course.name, section: course.section }
                        : course
                )
            })
            .then(response => response.text())
            .then(text => {
                console.log(text);
                location.reload();
            });
        });
    }



    function formatCourseDetailsTimes(times) {
        if (!times || times.length === 0) return "N/A";

        return times
            .map(t => `${t.day} ${minutesToTime(t.startTime)} - ${minutesToTime(t.endTime)}`)
            .join("<br>");
    }

    function favoriteCoursesContains(course) {
        const favorites = window.favoriteCourses || [];

        return favorites.some(f =>
            f.name === course.name &&
            f.section === course.section
        );
    }

    function getFreeBlockEnd(dayCourses, slotStart, endOfDay) {
        for (const course of dayCourses) {
            if (course.start >= slotStart) {
                return course.start;
            }
        }
        return endOfDay;
    }


});


    async function downloadPDF() {

        const { jsPDF } = window.jspdf;

        const calendar = document.querySelector(".calendar-wrapper");

        const canvas = await html2canvas(calendar, {scale: 2});

        const imgData = canvas.toDataURL("image/png");

        const pdf = new jsPDF({
            orientation: "portrait",
            unit: "in",
            format: "letter"
        });

       // Page dimensions
       const pageWidth = 11;   // inches
       const pageHeight = 8.5;

       // Margins
       const margin = 0.5;

       // Title
       pdf.setFont("helvetica", "bold");
       pdf.setFontSize(20);
       pdf.text("My Schedule", pageWidth / 2, margin, { align: "center" });

       // Space for title
       const titleHeight = 0.5;

       // Available space for image
       const availableWidth = pageWidth - (margin * 2);
       const availableHeight = pageHeight - margin - titleHeight - margin;

       // Convert canvas size to inches ratio
       const imgAspectRatio = canvas.width / canvas.height;

       let imgWidth = availableWidth;
       let imgHeight = imgWidth / imgAspectRatio;

       // If too tall, scale down
       if (imgHeight > availableHeight) {
           imgHeight = availableHeight;
           imgWidth = imgHeight * imgAspectRatio;
       }

       // Center image
       const x = (pageWidth - imgWidth) / 2;
       const y = margin + titleHeight;

       pdf.addImage(imgData, "PNG", x, y, imgWidth, imgHeight);

       pdf.save("schedule.pdf");
    }
