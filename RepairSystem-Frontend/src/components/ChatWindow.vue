<template>
<div class="chat-overlay" :class="{ 'blocking': dockMode === 'modal' }" @click.self="$emit('close')">
    <div 
        class="chat-window glass-panel"
        :class="[`dock-${dockMode}`, { 'is-dragging': isDragging }]"
        :style="windowStyle"
        ref="chatWindow"
    >
        <!-- Resize Handles -->
        <div v-if="canResize('top')" class="resize-handle handle-top" @mousedown.stop.prevent="startResize('top', $event)"></div>
        <div v-if="canResize('right')" class="resize-handle handle-right" @mousedown.stop.prevent="startResize('right', $event)"></div>
        <div v-if="canResize('bottom')" class="resize-handle handle-bottom" @mousedown.stop.prevent="startResize('bottom', $event)"></div>
        <div v-if="canResize('left')" class="resize-handle handle-left" @mousedown.stop.prevent="startResize('left', $event)"></div>
        
        <!-- Corners for window mode -->
        <div v-if="dockMode === 'window'" class="resize-handle handle-tl" @mousedown.stop.prevent="startResize('tl', $event)"></div>
        <div v-if="dockMode === 'window'" class="resize-handle handle-tr" @mousedown.stop.prevent="startResize('tr', $event)"></div>
        <div v-if="dockMode === 'window'" class="resize-handle handle-bl" @mousedown.stop.prevent="startResize('bl', $event)"></div>
        <div v-if="dockMode === 'window'" class="resize-handle handle-br" @mousedown.stop.prevent="startResize('br', $event)"></div>

        <div class="chat-header" @mousedown="startDrag">
            <div class="header-title">
                <h3>{{ partnerName || 'Chat' }}</h3>
                <span v-if="dockMode === 'window'" class="drag-hint">⋮⋮</span>
            </div>
            
            <div class="window-controls">
                <button class="control-btn" @click="setDockMode('left')" title="Dock Left" :class="{ active: dockMode === 'left' }">
                    <span class="icon">◧</span>
                </button>
                <button class="control-btn" @click="setDockMode('bottom')" title="Dock Bottom" :class="{ active: dockMode === 'bottom' }">
                    <span class="icon">◨</span>
                </button>
                <button class="control-btn" @click="setDockMode('right')" title="Dock Right" :class="{ active: dockMode === 'right' }">
                    <span class="icon">◧</span>
                </button>
                <button class="control-btn" @click="setDockMode('window')" title="Undock" :class="{ active: dockMode === 'window' }">
                    <span class="icon">❐</span>
                </button>
                <div class="divider"></div>
                <button class="control-btn close-btn" @click="$emit('close')" title="Close">×</button>
            </div>
        </div>

        <div class="messages-container" ref="messagesContainer">
            <div v-if="messages.length === 0" class="empty-chat">
                <p>No messages yet. Start the conversation!</p>
            </div>
            
            <div 
                v-for="msg in messages" 
                :key="msg.id" 
                class="message-wrapper"
                :class="{ 'my-message': isMe(msg), 'their-message': !isMe(msg) }"
            >
                <div class="message-bubble">
                    {{ msg.content }}
                </div>
                <div class="message-meta">
                    <span class="timestamp">{{ formatTime(msg.timestamp) }}</span>
                    <span v-if="isMe(msg) && msg.seen" class="seen-indicator">Seen</span>
                </div>
            </div>
        </div>

        <div class="chat-input-area">
            <input 
                v-model="newMessage" 
                @keyup.enter="sendMessage"
                type="text" 
                placeholder="Type a message..."
                class="chat-input"
            />
            <button class="send-btn" @click="sendMessage" :disabled="!newMessage.trim()">
                ➤
            </button>
        </div>
    </div>
</div>
</template>

<script>
import axios from 'axios'
var config = require('../../config')

var frontendUrl = 'http://' + config.dev.host + ':' + config.dev.port
var backendUrl = config.dev.backendHost
var AXIOS = axios.create({
    baseURL: backendUrl,
    headers: { 'Access-Control-Allow-Origin': frontendUrl }
})

export default {
    props: {
        partnerId: {
            type: Number,
            required: true
        },
        currentUserId: {
            type: Number,
            required: true
        },
        partnerName: {
            type: String,
            default: 'Chat'
        },
        initialPosition: {
            type: Object,
            default: null
        }
    },
    data() {
        return {
            messages: [],
            newMessage: '',
            pollingInterval: null,
            
            // Window State
            dockMode: 'window', // Default to window mode for multiple chats
            rect: {
                x: 0,
                y: 0,
                width: 350,
                height: 500
            },
            
            // Interaction State
            isDragging: false,
            resizeDir: null,
            dragOffset: { x: 0, y: 0 },
            initialRect: null
        }
    },
    computed: {
        windowStyle() {
            if (this.dockMode === 'window') {
                return {
                    left: `${this.rect.x}px`,
                    top: `${this.rect.y}px`,
                    width: `${this.rect.width}px`,
                    height: `${this.rect.height}px`,
                    zIndex: this.isDragging ? 1001 : 1000 // Bring to front on drag
                };
            } else if (this.dockMode === 'right') {
                return { width: `${this.rect.width}px` };
            } else if (this.dockMode === 'left') {
                return { width: `${this.rect.width}px` };
            } else if (this.dockMode === 'bottom') {
                return { height: `${this.rect.height}px` };
            }
            return {};
        }
    },
    created() {
        this.fetchMessages();
        this.pollingInterval = setInterval(this.fetchMessages, 3000);
        
        // Initialize window position
        if (this.initialPosition) {
            this.rect.x = this.initialPosition.x;
            this.rect.y = this.initialPosition.y;
        } else if (window.innerWidth) {
            this.rect.x = (window.innerWidth - 350) / 2;
            this.rect.y = (window.innerHeight - 500) / 2;
        }
    },
    beforeDestroy() {
        if (this.pollingInterval) clearInterval(this.pollingInterval);
        // Clean up listeners if component is destroyed while dragging
        window.removeEventListener('mousemove', this.handleMouseMove);
        window.removeEventListener('mouseup', this.handleMouseUp);
    },
    methods: {
        // ... Existing Chat Methods ...
        fetchMessages() {
            AXIOS.get('/api/chat', {
                params: {
                    user1Id: this.currentUserId,
                    user2Id: this.partnerId
                }
            })
                .then(response => {
                    this.messages = response.data;
                    this.scrollToBottom();
                    this.markAsSeen();
                })
                .catch(e => console.error(e));
        },
        markAsSeen() {
            const hasUnseen = this.messages.some(msg => !this.isMe(msg) && !msg.seen);
            if (hasUnseen) {
                AXIOS.put('/api/chat/seen', null, {
                    params: {
                        senderId: this.partnerId,
                        receiverId: this.currentUserId
                    }
                }).catch(e => console.error(e));
            }
        },
        sendMessage() {
            if (!this.newMessage.trim()) return;
            const payload = {
                content: this.newMessage,
                senderId: this.currentUserId,
                receiverId: this.partnerId
            };
            AXIOS.post('/api/chat', payload)
                .then(response => {
                    this.messages.push(response.data);
                    this.newMessage = '';
                    this.scrollToBottom();
                })
                .catch(e => console.error(e));
        },
        isMe(msg) {
            return msg.senderId === this.currentUserId;
        },
        formatTime(timestamp) {
            if (!timestamp) return '';
            return new Date(timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
        },
        scrollToBottom() {
            this.$nextTick(() => {
                const container = this.$refs.messagesContainer;
                if (container) {
                    container.scrollTop = container.scrollHeight;
                }
            });
        },

        // ... Window Management Methods ...
        setDockMode(mode) {
            this.dockMode = mode;
            // Reset dimensions to defaults for new mode if needed
            if (mode === 'bottom') {
                this.rect.height = 300;
            } else if (mode === 'right' || mode === 'left') {
                this.rect.width = 350;
            } else if (mode === 'window') {
                this.rect.width = 350;
                this.rect.height = 500;
                // Center if off screen
                if (this.rect.x < 0 || this.rect.x > window.innerWidth - 100) {
                    this.rect.x = (window.innerWidth - 350) / 2;
                    this.rect.y = (window.innerHeight - 500) / 2;
                }
            }
        },
        
        canResize(side) {
            if (this.dockMode === 'window') return true;
            if (this.dockMode === 'right' && side === 'left') return true;
            if (this.dockMode === 'left' && side === 'right') return true;
            if (this.dockMode === 'bottom' && side === 'top') return true;
            return false;
        },

        startDrag(e) {
            if (this.dockMode !== 'window') return;
            // Only drag if clicking header, not controls
            if (e.target.closest('.window-controls')) return;
            
            this.isDragging = true;
            this.dragOffset.x = e.clientX - this.rect.x;
            this.dragOffset.y = e.clientY - this.rect.y;
            
            window.addEventListener('mousemove', this.handleMouseMove);
            window.addEventListener('mouseup', this.handleMouseUp);
        },

        startResize(dir, e) {
            this.resizeDir = dir;
            this.initialRect = { ...this.rect };
            this.dragOffset.x = e.clientX;
            this.dragOffset.y = e.clientY;
            
            window.addEventListener('mousemove', this.handleMouseMove);
            window.addEventListener('mouseup', this.handleMouseUp);
        },

        handleMouseMove(e) {
            if (this.isDragging) {
                this.rect.x = e.clientX - this.dragOffset.x;
                this.rect.y = e.clientY - this.dragOffset.y;
                
                // Constraints
                this.rect.y = Math.max(0, this.rect.y);
                this.rect.y = Math.min(window.innerHeight - 50, this.rect.y);
                this.rect.x = Math.max(0 - this.rect.width + 50, this.rect.x);
                this.rect.x = Math.min(window.innerWidth - 50, this.rect.x);
            } else if (this.resizeDir) {
                const dx = e.clientX - this.dragOffset.x;
                const dy = e.clientY - this.dragOffset.y;
                
                const minW = 300;
                const minH = 200;

                if (this.dockMode === 'window') {
                    if (this.resizeDir.includes('r')) this.rect.width = Math.max(minW, this.initialRect.width + dx);
                    if (this.resizeDir.includes('l')) {
                        const newW = Math.max(minW, this.initialRect.width - dx);
                        this.rect.x = this.initialRect.x + (this.initialRect.width - newW);
                        this.rect.width = newW;
                    }
                    if (this.resizeDir.includes('b')) this.rect.height = Math.max(minH, this.initialRect.height + dy);
                    if (this.resizeDir.includes('t')) {
                        const newH = Math.max(minH, this.initialRect.height - dy);
                        this.rect.y = this.initialRect.y + (this.initialRect.height - newH);
                        this.rect.height = newH;
                    }
                } else if (this.dockMode === 'right') {
                    this.rect.width = Math.max(minW, this.initialRect.width - dx);
                } else if (this.dockMode === 'left') {
                    this.rect.width = Math.max(minW, this.initialRect.width + dx);
                } else if (this.dockMode === 'bottom') {
                    this.rect.height = Math.max(minH, this.initialRect.height - dy);
                }
            }
        },

        handleMouseUp() {
            this.isDragging = false;
            this.resizeDir = null;
            window.removeEventListener('mousemove', this.handleMouseMove);
            window.removeEventListener('mouseup', this.handleMouseUp);
        }
    }
}

</script>

<style scoped>
.chat-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    z-index: 1000;
    pointer-events: none; /* Let clicks pass through to app */
}

.chat-overlay.blocking {
    pointer-events: auto;
    background: rgba(0, 0, 0, 0.3);
}

.chat-window {
    position: absolute;
    background: #1a202c;
    background: linear-gradient(180deg, rgba(30, 41, 59, 0.98) 0%, rgba(15, 23, 42, 0.99) 100%);
    backdrop-filter: blur(10px);
    display: flex;
    flex-direction: column;
    box-shadow: 0 0 25px rgba(0, 0, 0, 0.5);
    pointer-events: auto;
    transition: box-shadow 0.2s;
    border: 1px solid rgba(255, 255, 255, 0.1);
}

.chat-window.is-dragging {
    user-select: none;
    box-shadow: 0 0 35px rgba(0, 0, 0, 0.7);
    border-color: var(--primary);
}

/* Dock Modes */
.dock-right {
    top: 0;
    right: 0;
    height: 100%;
    border-left: 1px solid rgba(255, 255, 255, 0.1);
}

.dock-left {
    top: 0;
    left: 0;
    height: 100%;
    border-right: 1px solid rgba(255, 255, 255, 0.1);
}

.dock-bottom {
    bottom: 0;
    left: 0;
    width: 100%;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.dock-window {
    border-radius: 8px;
    /* Positioned via inline styles */
}

/* Header & Controls */
.chat-header {
    padding: 0.8rem 1rem;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    display: flex;
    justify-content: space-between;
    align-items: center;
    cursor: default;
    background: rgba(255, 255, 255, 0.02);
}

.dock-window .chat-header {
    cursor: move;
}

.header-title {
    display: flex;
    align-items: center;
    gap: 0.5rem;
}

.header-title h3 {
    margin: 0;
    font-size: 1rem;
    color: var(--text-primary);
    font-weight: 600;
}

.drag-hint {
    color: var(--text-secondary);
    font-size: 1.2rem;
    opacity: 0.5;
    letter-spacing: -2px;
}

.window-controls {
    display: flex;
    align-items: center;
    gap: 0.25rem;
}

.control-btn {
    background: none;
    border: none;
    color: var(--text-secondary);
    padding: 4px;
    border-radius: 4px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s;
}

.control-btn:hover {
    background: rgba(255, 255, 255, 0.1);
    color: white;
}

.control-btn.active {
    color: var(--primary);
    background: rgba(255, 255, 255, 0.05);
}

.control-btn .icon {
    font-size: 1rem;
    line-height: 1;
}

/* Rotate icons for different dock positions */
.control-btn[title="Dock Right"] .icon { transform: rotate(180deg); }
.control-btn[title="Dock Bottom"] .icon { transform: rotate(-90deg); }

.divider {
    width: 1px;
    height: 16px;
    background: rgba(255, 255, 255, 0.1);
    margin: 0 0.5rem;
}

.close-btn {
    font-size: 1.2rem;
    padding: 0 4px;
}

.close-btn:hover {
    background: rgba(239, 68, 68, 0.2);
    color: #ef4444;
}

/* Resize Handles */
.resize-handle {
    position: absolute;
    z-index: 100;
    /* background: rgba(255, 0, 0, 0.2); Debugging */
}

.handle-top { top: -3px; left: 0; right: 0; height: 6px; cursor: ns-resize; }
.handle-bottom { bottom: -3px; left: 0; right: 0; height: 6px; cursor: ns-resize; }
.handle-left { left: -3px; top: 0; bottom: 0; width: 6px; cursor: ew-resize; }
.handle-right { right: -3px; top: 0; bottom: 0; width: 6px; cursor: ew-resize; }

.handle-tl { top: -5px; left: -5px; width: 10px; height: 10px; cursor: nwse-resize; }
.handle-tr { top: -5px; right: -5px; width: 10px; height: 10px; cursor: nesw-resize; }
.handle-bl { bottom: -5px; left: -5px; width: 10px; height: 10px; cursor: nesw-resize; }
.handle-br { bottom: -5px; right: -5px; width: 10px; height: 10px; cursor: nwse-resize; }

/* Content Area */
.messages-container {
    flex: 1;
    overflow-y: auto;
    padding: 1rem;
    display: flex;
    flex-direction: column;
    gap: 0.8rem;
}

/* ... Keep existing message styles ... */
.empty-chat {
    text-align: center;
    color: var(--text-secondary);
    margin-top: 2rem;
    font-style: italic;
}

.message-wrapper {
    display: flex;
    flex-direction: column;
    max-width: 85%;
}

.my-message {
    align-self: flex-end;
    align-items: flex-end;
}

.their-message {
    align-self: flex-start;
    align-items: flex-start;
}

.message-bubble {
    padding: 0.6rem 0.9rem;
    border-radius: 12px;
    font-size: 0.9rem;
    line-height: 1.4;
    word-wrap: break-word;
}

.my-message .message-bubble {
    background: var(--primary);
    color: white;
    border-bottom-right-radius: 2px;
}

.their-message .message-bubble {
    background: rgba(255, 255, 255, 0.1);
    color: var(--text-primary);
    border-bottom-left-radius: 2px;
}

.message-meta {
    display: flex;
    gap: 0.5rem;
    margin-top: 0.2rem;
    font-size: 0.7rem;
    color: var(--text-secondary);
    opacity: 0.8;
}

.seen-indicator {
    color: var(--primary);
    font-weight: bold;
}

.chat-input-area {
    padding: 1rem;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
    display: flex;
    gap: 0.5rem;
    background: rgba(0, 0, 0, 0.2);
}

.chat-input {
    flex: 1;
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 20px;
    padding: 0.6rem 1rem;
    color: white;
    outline: none;
    transition: all 0.2s;
    font-size: 0.9rem;
}

.chat-input:focus {
    background: rgba(255, 255, 255, 0.1);
    border-color: var(--primary);
}

.send-btn {
    background: var(--primary);
    border: none;
    width: 38px;
    height: 38px;
    border-radius: 50%;
    color: white;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1rem;
    transition: transform 0.2s;
}

.send-btn:hover:not(:disabled) {
    transform: scale(1.05);
}

.send-btn:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}
</style>
