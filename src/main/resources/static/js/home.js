let stompClient = null;
let username = null;

function connect() {
    username = document.getElementById('username-input').value.trim();
    if (username) {
        document.getElementById('username-modal').style.display = 'none';

        let socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
}

function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public', onMessageReceived);

    // Tell your username to the server
    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    );

    document.getElementById('connection-status').textContent = 'En Ligne';
}

function onError(error) {
    document.getElementById('connection-status').textContent = 'Connection failed. Please refresh the page.';
}

function sendMessage() {
    let messageInput = document.getElementById('message-input');
    let messageContent = messageInput.value.trim();

    if (messageContent && stompClient) {
        let chatMessage = {
            sender: username,
            content: messageContent,
            type: 'CHAT'
        };

        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
}

function handleKeyPress(event) {
    if (event.key === 'Enter') {
        sendMessage();
    }
}

function onMessageReceived(payload) {
    let message = JSON.parse(payload.body);
    let messageContainer = document.getElementById('messages-container');

    let messageElement = document.createElement('div');

    if (message.type === 'JOIN') {
        messageElement.className = 'flex justify-center';
        messageElement.innerHTML = `
            <div class="bg-gray-200 px-4 py-2 rounded-full">
                <p class="text-sm text-gray-600">${message.sender} joindre a cette Conversation</p>
            </div>
        `;
    } else if (message.type === 'LEAVE') {
        messageElement.className = 'flex justify-center';
        messageElement.innerHTML = `
            <div class="bg-gray-200 px-4 py-2 rounded-full">
                <p class="text-sm text-gray-600">${message.sender} Quitter cette Conversation</p>
            </div>
        `;
    } else {
        let isMyMessage = message.sender === username;
        messageElement.className = isMyMessage ? 'flex justify-end space-x-3' : 'flex items-start space-x-3';

        messageElement.innerHTML = `
            ${!isMyMessage ? `<div class="w-10 h-10 rounded-full bg-gray-300 flex items-center justify-center">
                <span class="text-sm font-bold">${message.sender.charAt(0)}</span>
            </div>` : ''}
            <div class="${isMyMessage ? 'bg-blue-500 text-white' : 'bg-gray-200'} p-3 rounded-lg max-w-[70%]">
                <p class="text-sm">${message.content}</p>
                <span class="text-xs ${isMyMessage ? 'text-blue-200' : 'text-gray-500'} block mt-1">
                    ${new Date().toLocaleTimeString()}
                </span>
            </div>
        `;
    }

    messageContainer.appendChild(messageElement);
    messageContainer.scrollTop = messageContainer.scrollHeight;
}

// Auto-focus username input when page loads
window.onload = function () {
    document.getElementById('username-input').focus();
};