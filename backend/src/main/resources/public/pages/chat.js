document.addEventListener("DOMContentLoaded", function () {
    const CHAT_STORAGE_KEY = "cement_chat_history";

    function saveMessages(messages) {
        sessionStorage.setItem(CHAT_STORAGE_KEY, JSON.stringify(messages));
    }

    function loadMessages() {
        const stored = sessionStorage.getItem(CHAT_STORAGE_KEY);

        if (!stored) {
            return [];
        }

        try {
            return JSON.parse(stored);
        } catch (error) {
            console.error("Failed to parse chat history:", error);
            return [];
        }
    }

    if (document.getElementById("chat-toggle") || document.getElementById("chat-window")) {
        return;
    }

    document.body.insertAdjacentHTML("beforeend", `
        <button id="chat-toggle" type="button">Chat</button>

        <div id="chat-window" class="chat-hidden">
            <div id="chat-header">
                <span>CEMENT Assistant</span>
                <button id="chat-close" type="button">&times;</button>
            </div>

            <div id="chat-messages"></div>

            <div id="chat-input-area">
                <input
                    type="text"
                    id="chat-input"
                    placeholder="Ask about schedules, filters, or courses..."
                >
                <button id="chat-send" type="button">Send</button>
            </div>
        </div>
    `);

    const style = document.createElement("style");
    style.textContent = `
        #chat-toggle {
            position: fixed;
            bottom: 20px;
            right: 20px;
            width: 68px;
            height: 68px;
            border: none;
            border-radius: 50%;
            background: #1f3a5f;
            color: white;
            font-weight: 600;
            cursor: pointer;
            box-shadow: 0 8px 20px rgba(0,0,0,0.2);
            z-index: 2000;
        }

        #chat-window {
            position: fixed;
            bottom: 20px;
            right: 20px;
            width: 360px;
            height: 480px;
            background: white;
            border-radius: 16px;
            box-shadow: 0 12px 30px rgba(0,0,0,0.22);
            display: flex;
            flex-direction: column;
            overflow: hidden;
            z-index: 2001;
            border: 1px solid #d9dee5;
        }

        .chat-hidden {
            display: none !important;
        }

        #chat-header {
            height: 56px;
            background: #1f3a5f;
            color: white;
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 14px;
            font-size: 18px;
            font-weight: 600;
        }

        #chat-close {
            background: transparent;
            border: none;
            color: white;
            font-size: 26px;
            cursor: pointer;
            line-height: 1;
        }

        #chat-messages {
            flex: 1;
            overflow-y: auto;
            padding: 14px;
            background: #f6f8fb;
            display: flex;
            flex-direction: column;
            gap: 10px;
        }

        .chat-message {
            max-width: 82%;
            padding: 10px 12px;
            border-radius: 14px;
            line-height: 1.4;
            word-wrap: break-word;
        }

        .user-message {
            align-self: flex-end;
            background: #dbeafe;
            color: #1e293b;
        }

        .bot-message {
            align-self: flex-start;
            background: white;
            border: 1px solid #dbe3ec;
            color: #1e293b;
        }

        .chat-course {
            align-self: flex-start;
            background: #f1f5f9;
            border: 1px solid #dbe3ec;
            padding: 8px 10px;
            border-radius: 10px;
            font-size: 12px;
            line-height: 1.4;
            max-width: 90%;
        }

        .chat-add-course-btn {
            margin-top: 8px;
            border: none;
            border-radius: 8px;
            background: #1f3a5f;
            color: white;
            padding: 6px 10px;
            font-size: 12px;
            cursor: pointer;
        }

        .chat-add-course-btn:disabled {
            background: #64748b;
            cursor: default;
        }

        #chat-input-area {
            display: flex;
            gap: 8px;
            padding: 12px;
            border-top: 1px solid #e2e8f0;
            background: white;
        }

        #chat-input {
            flex: 1;
            border: 1px solid #cbd5e1;
            border-radius: 10px;
            padding: 10px;
            outline: none;
        }

        #chat-input:focus {
            border-color: #1f3a5f;
        }

        #chat-send {
            border: none;
            border-radius: 10px;
            background: #1f3a5f;
            color: white;
            padding: 0 16px;
            cursor: pointer;
            font-weight: 600;
        }

        #chat-send:hover,
        #chat-toggle:hover {
            filter: brightness(0.95);
        }

        @media (max-width: 500px) {
            #chat-window {
                width: calc(100vw - 20px);
                height: 70vh;
                right: 10px;
                bottom: 10px;
            }

            #chat-toggle {
                right: 10px;
                bottom: 10px;
            }
        }
    `;
    document.head.appendChild(style);

    const chatToggle = document.getElementById("chat-toggle");
    const chatWindow = document.getElementById("chat-window");
    const chatClose = document.getElementById("chat-close");
    const chatInput = document.getElementById("chat-input");
    const chatSend = document.getElementById("chat-send");
    const chatMessages = document.getElementById("chat-messages");

    let messageHistory = loadMessages();

    function addMessage(text, className, shouldSave = true) {
        const messageDiv = document.createElement("div");
        messageDiv.classList.add("chat-message", className);
        messageDiv.textContent = text;
        chatMessages.appendChild(messageDiv);
        chatMessages.scrollTop = chatMessages.scrollHeight;

        if (shouldSave) {
            messageHistory.push({
                type: "message",
                text: text,
                className: className
            });
            saveMessages(messageHistory);
        }
    }

    function addCourseCard(course, shouldSave = true) {
        const div = document.createElement("div");
        div.className = "chat-course";

        const courseKey = `${course.name}|${course.section}`;
        const alreadyScheduled =
            window.scheduledCourses && window.scheduledCourses.has(courseKey);

        div.innerHTML = `
            <strong>${course.courseCode}</strong> - ${course.name}<br>
            Section ${course.section}<br>
            ${course.credits} credits<br>
            ${course.professors?.[0] || ""}<br>
            <button type="button" class="chat-add-course-btn">
                ${alreadyScheduled ? "Already added" : "Add course"}
            </button>
        `;

        const button = div.querySelector(".chat-add-course-btn");

        if (alreadyScheduled) {
            button.disabled = true;
        }

        button.addEventListener("click", async function () {
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
                    button.disabled = true;

                    if (window.scheduledCourses) {
                        window.scheduledCourses.add(courseKey);
                    }

                    if (typeof window.refreshSchedule === "function") {
                        window.refreshSchedule();
                    }
                } else {
                    const errorText = await response.text();
                    console.error("Could not add course:", errorText);
                    button.textContent = "Could not add";
                }
            } catch (error) {
                console.error("Add course failed:", error);
                button.textContent = "Error";
            }
        });

        chatMessages.appendChild(div);
        chatMessages.scrollTop = chatMessages.scrollHeight;

        if (shouldSave) {
            messageHistory.push({
                type: "course",
                course: course
            });
            saveMessages(messageHistory);
        }
    }

    function displayCourses(courses, shouldSave = true) {
        courses.slice(0, 5).forEach(course => {
            addCourseCard(course, shouldSave);
        });
    }

    function sendMessage() {
        const userMessage = chatInput.value.trim();

        if (!userMessage) {
            return;
        }

        addMessage(userMessage, "user-message");
        chatInput.value = "";

        fetch("/api/chat", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                message: userMessage,
                semester: sessionStorage.getItem("selectedSemester")
            })
        })
            .then(async response => {
                if (!response.ok) {
                    const errorText = await response.text();
                    console.error("Chat backend error:", errorText);
                    addMessage("Server error: " + errorText, "bot-message");
                    return null;
                }

                return response.json();
            })
            .then(data => {
                if (!data) {
                    return;
                }

                addMessage(data.reply, "bot-message");

                if ((data.intent === "COURSE_SEARCH" || data.intent === "SCHEDULE_SUGGESTION") && data.data) {
                    displayCourses(data.data);
                }
            })
            .catch(err => {
                console.error("Chat fetch failed:", err);
                addMessage("Error contacting server.", "bot-message");
            });
    }

    chatToggle.addEventListener("click", function () {
        chatWindow.classList.remove("chat-hidden");
        chatToggle.classList.add("chat-hidden");
        chatInput.focus();
    });

    chatClose.addEventListener("click", function () {
        chatWindow.classList.add("chat-hidden");
        chatToggle.classList.remove("chat-hidden");
    });

    chatSend.addEventListener("click", sendMessage);

    chatInput.addEventListener("keydown", function (event) {
        if (event.key === "Enter") {
            event.preventDefault();
            sendMessage();
        }
    });

    if (messageHistory.length > 0) {
        messageHistory.forEach(entry => {
            if (entry.type === "course" && entry.course) {
                addCourseCard(entry.course, false);
            } else if (entry.text && entry.className) {
                addMessage(entry.text, entry.className, false);
            }
        });
    } else {
        addMessage("Hi! I can help with schedules, course searches, and filters.", "bot-message");
    }
});