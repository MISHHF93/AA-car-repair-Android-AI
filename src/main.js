import './styles.css'

const app = document.querySelector('#app')

app.innerHTML = `
  <main class="chat-page">
    <header class="chat-header">
      <p class="eyebrow">AA Car Repair Android AI</p>
      <h1>AI Chatbot</h1>
    </header>

    <section class="chat-layout">
      <div id="message-list" class="message-list" aria-live="polite"></div>

      <form id="composer" class="composer">
        <label class="composer-label" for="chat-input">Ask a diagnostic question</label>
        <div class="composer-row">
          <input id="chat-input" name="query" type="text" placeholder="Type a DTC like P0171" autocomplete="off" required />
          <button id="send-button" type="submit">Send</button>
        </div>
        <p id="status" class="status"></p>
      </form>
    </section>
  </main>
`

const messageList = document.querySelector('#message-list')
const composer = document.querySelector('#composer')
const input = document.querySelector('#chat-input')
const status = document.querySelector('#status')
const sendButton = document.querySelector('#send-button')

const addMessage = (role, content, meta = '') => {
  const row = document.createElement('article')
  row.className = `message message-${role}`
  row.innerHTML = `
    <p class="message-role">${role === 'user' ? 'You' : 'Assistant'}</p>
    <p class="message-body"></p>
    ${meta ? `<p class="message-meta">${meta}</p>` : ''}
  `
  row.querySelector('.message-body').textContent = content
  messageList.appendChild(row)
  messageList.scrollTop = messageList.scrollHeight
}

const buildRequest = (queryText) => ({
  request_id: crypto.randomUUID(),
  timestamp_utc: new Date().toISOString(),
  surface: 'mobile',
  user_role: 'consumer',
  locale: 'en-CA',
  query_text: queryText,
  policy_profile: 'mobile_default',
  privacy_mode: 'standard'
})

const sendChatRequest = async (payload) => {
  const response = await fetch('/v1/chat', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  })

  if (!response.ok) {
    throw new Error(`Chat request failed with status ${response.status}`)
  }

  return response.json()
}

composer.addEventListener('submit', async (event) => {
  event.preventDefault()
  const queryText = input.value.trim()
  if (!queryText) return

  const payload = buildRequest(queryText)
  addMessage('user', queryText)

  input.value = ''
  input.focus()
  status.textContent = 'Thinking...'
  status.className = 'status status-loading'
  sendButton.disabled = true

  try {
    const data = await sendChatRequest(payload)
    const meta = `confidence: ${data.confidence} • safety: ${data.safety_level} • trace: ${data.audit_trace_id}`
    addMessage('assistant', data.answer_text, meta)
    if (Array.isArray(data.citations) && data.citations.length > 0) {
      addMessage('assistant', `Citations: ${data.citations.join(' | ')}`)
    }
    status.textContent = ''
    status.className = 'status'
  } catch (error) {
    status.textContent = error.message
    status.className = 'status status-error'
  } finally {
    sendButton.disabled = false
  }
})

addMessage('assistant', 'Ask about a code (example: P0171) or a repair symptom to get started.')
