document.addEventListener("DOMContentLoaded", function () {

    console.log("Calendar page loaded!");
    console.log("My new code is working");

    const startOfDay = 8 * 60;   // 8:00 AM
    const endOfDay = 18 * 60;    // 6:00 PM

    // Day columns (must match HTML IDs)
    const days = { M: [], T: [], W: [], R: [], F: [] };

    const timeColumn = document.getElementById("timeColumn");
    const slotMinutes = 30;

    for (let t = startOfDay; t < endOfDay; t += slotMinutes) {
        const label = document.createElement("div");
        label.classList.add("time-label");
        label.textContent = minutesToTime(t);
        timeColumn.appendChild(label);
    }

    fetch("/api/schedule")
        .then(response => response.json())
        .then(courses => {

            // Assign courses to their day arrays
            courses.forEach(course => {
                course.times.forEach(time => {
                    if (!days[time.day]) return;

                    days[time.day].push({
                        name: course.name,
                        section: course.section,
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

                const slotMinutes = 30;

                for (let t = startOfDay; t < endOfDay; t += slotMinutes) {
                    const slot = document.createElement("div");
                    slot.classList.add("time-slot", "empty-slot");

                    const link = document.createElement("a");
                    link.classList.add("add-course-link");

                    const params = new URLSearchParams({
                        days: getDayGroup(day),
                        start: t,
                        end: t + slotMinutes
                    });

                    link.href = `/search?${params.toString()}`;
                    link.textContent = "+ Add";

                    slot.appendChild(link);
                    dayGrid.appendChild(slot);
                }

                dayCourses.forEach(course => {
                    addCourse(dayGrid, course);
                });
            });

        });



    function addCourse(dayGrid, course) {

        const slotMinutes = 30;

        const startRow =
            Math.floor((course.start - startOfDay) / slotMinutes) + 1;

        const rowSpan =
            Math.ceil((course.end - course.start) / slotMinutes);

        const div = document.createElement("div");
        div.classList.add("course-block");

        div.style.gridRow = `${startRow} / span ${rowSpan}`;

        div.innerHTML = `
            <div class="fw-semibold">${course.name} (${course.section})</div>
            <div>${minutesToTime(course.start)} - ${minutesToTime(course.end)}</div>
        `;

        const button = document.createElement("button");
        button.textContent = "Remove";
        button.classList.add("btn", "btn-sm", "btn-light", "mt-1");

        button.onclick = () => {
            fetch("/api/remove-course", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({
                    name: course.name,
                    section: course.section
                })
            }).then(() => location.reload());
        };

        div.appendChild(button);

        dayGrid.appendChild(div);
    }



    //This should most likely be in the backend instead of the front end.
    //Leaving this here for right now until we make a finalized
    //decision on the format of time in the database

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



});