function sendQuestion() {
    var question = document.getElementById("userQuestion").value;
    fetch("/chatbot/ask", {
        method: "POST",
        body: JSON.stringify({ question: question }),
        headers: {
            "Content-Type": "application/json"
        }
    })
    .then(response => response.json())
    .then(data => {
        var messages = document.getElementById("messages");
        messages.innerHTML += "<div class='question'>" + question + "</div>";
        messages.innerHTML += "<div class='answer'>" + data.answer + "</div>";
    });
}

function toggleChat() {
    const chatPopup = document.getElementById("chat-popup");
    if (chatPopup.style.display === "none" || chatPopup.style.display === "") {
        chatPopup.style.display = "flex"; // 보이기
    } else {
        chatPopup.style.display = "none"; // 숨기기
    }
}
