document.addEventListener("DOMContentLoaded", function () {
    // Avoid adding the chat twice
    if (document.getElementById("chat-toggle") || document.getElementById("chat-window")) {
        return;
    }

    // Add chat HTML to the page
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

    // Add chat CSS to the page
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

    function addMessage(text, className) {
        const messageDiv = document.createElement("div");
        messageDiv.classList.add("chat-message", className);
        messageDiv.textContent = text;
        chatMessages.appendChild(messageDiv);
        chatMessages.scrollTop = chatMessages.scrollHeight;
    }

    async function sendMessage() {
        const message = chatInput.value.trim();
        if (message === "") return;

        addMessage(message, "user-message");
        chatInput.value = "";

        try {
            const response = await fetch("/api/chat", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    message: message,
                    studentId: "12345"
                })
            });

            if (!response.ok) {
                const errorText = await response.text();
                console.error("Chat backend error:", response.status, errorText);
                addMessage("Backend error.", "bot-message");
                return;
            }

            const data = await response.json();
            addMessage(data.reply || "No response from chatbot.", "bot-message");
        } catch (error) {
            console.error("Chat fetch failed:", error);
            addMessage("Server error. Please try again.", "bot-message");
        }
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

    addMessage("Hi! I can help with schedules, course searches, and filters.", "bot-message");
});