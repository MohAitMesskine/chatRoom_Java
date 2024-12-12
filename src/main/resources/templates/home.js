function sendMessage() {
    const input = document.getElementById('message-input');
    const container = document.getElementById('messages-container');

    if (input.value.trim() === '') return;

    const messageDiv = document.createElement('div');
    messageDiv.className = 'flex justify-end space-x-3';
    messageDiv.innerHTML = `
                <div class="bg-blue-500 text-white p-3 rounded-lg max-w-[70%]">
                    <p class="text-sm">${input.value}</p>
                    <span class="text-xs text-blue-200 block mt-1">${new Date().toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}</span>
                </div>
            `;

    container.appendChild(messageDiv);
    input.value = '';

    // Scroll automatiquement vers le bas
    container.scrollTop = container.scrollHeight;
}

// Permettre l'envoi de message avec la touche Entrée
document.getElementById('message-input').addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
        sendMessage();
    }
});