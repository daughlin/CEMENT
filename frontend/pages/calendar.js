document.addEventListener("DOMContentLoaded", function () {

    console.log("Calendar page loaded!");

    const startOfDay = 8 * 60;   // 8:00 AM
    const endOfDay = 18 * 60;    // 6:00 PM

    // Day columns (must match HTML IDs)
    const days = { M: [], T: [], W: [], R: [], F: [], S: [] };

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

            // Process each day
            Object.keys(days).forEach(day => {

                //const dayBox = document.getElementById(day);
                const dayBox = document.querySelector(`#${day} .card-body`);
                if (!dayBox) return;

                const dayCourses = days[day];

                const pixelsPerMinute = dayBox.clientHeight / (endOfDay - startOfDay);

                // Sort courses by start time
                dayCourses.sort((a, b) => a.start - b.start);

                let previousEnd = startOfDay;

                dayCourses.forEach(course => {

                    // If there is free time before this course
                    if (course.start > previousEnd) {
                        addFreeBlock(dayBox, day, previousEnd, course.start, pixelsPerMinute);
                    }

                    // Add the course block
                    addCourse(dayBox, course, pixelsPerMinute);

                    previousEnd = course.end;
                });

                // Free time after the last course
                if (previousEnd < endOfDay) {
                    addFreeBlock(dayBox, day, previousEnd, endOfDay, pixelsPerMinute);
                }

            });

        });

    // Add a course block
    function addCourse(dayBox, course, pixelsPerMinute) {

        const dayHeaderHeight = 60;
        const top = (course.start - startOfDay) * pixelsPerMinute + dayHeaderHeight;
        const height = (course.end - course.start) * pixelsPerMinute;

        const div = document.createElement("div");
        div.classList.add("course");

        div.style.position = "absolute";
        div.style.top = top + "px";
        div.style.height = height + "px";
        div.style.left = "5px";
        div.style.right = "5px";
        div.style.zIndex = "2";

        // Course name
        const title = document.createElement("div");
        title.textContent = `${course.name} (${course.section})`;

        // Time display
        const time = document.createElement("div");
        time.textContent =
            `${minutesToTime(course.start)} - ${minutesToTime(course.end)}`;

        // Remove button
        const button = document.createElement("button");
        button.textContent = "Remove";
        button.style.marginTop = "5px";

        button.onclick = () => {

               fetch("/api/remove-course", {
                   method: "POST",
                   headers: {
                       "Content-Type": "application/json"
                   },
                   body: JSON.stringify({
                       name: course.name,
                       section: course.section
                   })
               })
               .then(() => location.reload()); // refresh calendar
        };

        // Append elements
        div.appendChild(title);
        div.appendChild(time);
        div.appendChild(button);

        dayBox.appendChild(div);
    }

    // Add a free slot with a button
    function addFreeBlock(dayBox, day, start, end, pixelsPerMinute) {

        const dayHeaderHeight = 60;
        const top = (start - startOfDay) * pixelsPerMinute + dayHeaderHeight;
        const height = (end - start) * pixelsPerMinute;

        const div = document.createElement("div");
        div.classList.add("free-slot");

        div.style.position = "absolute";
        div.style.top = top + "px";
        div.style.height = height + "px";
        div.style.left = "5px";
        div.style.right = "5px";
        div.style.zIndex = "1";

        // Time label
        const time = document.createElement("div");
        time.textContent = `${minutesToTime(start)} - ${minutesToTime(end)}`;

        // Button
        const link = document.createElement("a");
        link.textContent = "view courses";

        const interval = 15;

        const roundedStart = roundUpToInterval(start, interval);
        const roundedEnd = roundDownToInterval(end, interval);

        const groupedDays = getDayGroup(day);

        if (roundedStart >= roundedEnd) {
            link.href = `/search?days=${groupedDays}`;
        } else {
            const params = new URLSearchParams({
                days: groupedDays,
                start: roundedStart,
                end: roundedEnd
            });

            link.href = `/search?${params.toString()}`;
        }

        div.appendChild(time);
        div.appendChild(link);
        dayBox.appendChild(div);
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

    function roundUpToInterval(minutes, interval) {
        return Math.ceil(minutes / interval) * interval;
    }

    function roundDownToInterval(minutes, interval) {
        return Math.floor(minutes / interval) * interval;
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