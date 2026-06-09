let activeContact;
let contacts = [];
let messages = {};
let webSocket;
let jwtToken;
let currentUserId;
let unreadMessages = {}; // Track unread messages per contact


const baseUrl = 'http://localhost:8080';
const baseUrlWs = 'ws://localhost:8080';
const contactsEl = document.getElementById('contacts');
const messagesEl = document.getElementById('messages');
const headerEl = document.getElementById('chat-header');
const headerNameEl = document.getElementById('chatHeaderName');
const headerStatusEl = document.getElementById('chatHeaderStatus');
const composer = document.getElementById('composer');
const messageInput = document.getElementById('messageInput');
const searchInput = document.getElementById('search');
const sidebarBody = document.getElementById('sidebarBody');
const sidebarEmptyState = document.getElementById('sidebarEmptyState');
const addContactButton = document.getElementById('addContactButton');
const contactModal = document.getElementById('contactModal');
const closeContactModal = document.getElementById('closeContactModal');
const cancelContactButton = document.getElementById('cancelContactButton');
const addContactForm = document.getElementById('addContactForm');
const contactEmailInput = document.getElementById('contactEmail');
const sendBtn = document.getElementById('sendBtn');
const contactModalError = document.getElementById('contactModalError');
const noContactsMessage = document.getElementById('noContactsMessage');
const noContactsMessageText = noContactsMessage.querySelector('p');
const noConversationMessage = document.getElementById('noConversationMessage');

function redirectToLogin(status) {
  window.location.replace(`login.html?status=${status}`);
}

function isAuthError(response) {
  return response.status === 401 || response.status === 403;
}

async function checkProtectedResponse(response) {
  if (isAuthError(response)) {
    redirectToLogin(response.status);
  }
  return false;
}

async function fetchContacts() {
  try {
    const response = await fetch(`${baseUrl}/contacts`, {
      method: "GET",
      headers: {
        "Authorization": `Bearer ${jwtToken}`
      }
    });

    if (response.status != 302) {
      console.error('fetchContacts failed with status', response.status);
      return;
    }

    const body = await response.json();
    // preserve online statuses from the current local contacts store
    const statusMap = contacts.reduce((acc, contact) => {
      acc[contact.contactUserId] = !!contact.isOnline;
      return acc;
    }, {});

    const selectedContactId = activeContact?.contactUserId;

    contacts = [];
    if (Array.isArray(body)) {
      body.forEach(contact => {
        contact.isOnline = statusMap[contact.contactUserId] ?? false;
        contacts.push(contact);
        // Initialize unread count if not exists
        if (!(contact.contactUserId in unreadMessages)) {
          unreadMessages[contact.contactUserId] = 0;
        }
      });
      if (selectedContactId) {
        activeContact = contacts.find(c => String(c.contactUserId) === String(selectedContactId));
      }
      renderContacts(getFilteredContacts());
      preloadLastMessagesForContacts();
    } else {
      console.warn('fetchContacts: unexpected response', body);
      renderContacts([]);
    }
  }
  catch (error) {
    // request couldn't reach the server. Will handle this part later.
  }
}


function getContactById(contactUserId) {
  const idx = findContactIndexByAnyId(contactUserId);
  return idx === -1 ? undefined : contacts[idx];
}

function findContactIndexByAnyId(contactUserId) {
  const idStr = String(contactUserId);
  return contacts.findIndex(c => {
    return [c.contactUserId, c.id, c.userId, c.contactId].some(x => String(x) === idStr);
  });
}

function getFilteredContacts() {
  const q = searchInput.value.trim().toLowerCase();
  if (!q) return contacts;

  return contacts.filter(c => {
    const name = c.contactUserName?.toLowerCase() ?? '';
    const last = c.last?.toLowerCase() ?? '';
    const email = c.contactUserEmail?.toLowerCase() ?? '';
    const matchesSearch = name.includes(q) || last.includes(q) || email.includes(q);
    const hasUnread = unreadMessages[c.contactUserId] > 0;
    return matchesSearch || hasUnread;
  });
}

function normalizeActiveStatus(status) {
  return String(status || '').trim().toUpperCase() === 'ONLINE';
}

function updateContactStatusInDOM(contactUserId, isOnline) {
  const contactElement = document.querySelector(`.contact[data-id="${contactUserId}"]`);
  if (!contactElement) return;

  const statusDot = contactElement.querySelector('.contact-status');
  if (statusDot) {
    statusDot.className = 'contact-status ' + (isOnline ? 'online' : 'offline');
  }
}

function setContactOnlineStatus(contactUserId, isOnline) {
  const contact = getContactById(contactUserId);
  if (contact) {
    contact.isOnline = !!isOnline;
  }

  if (activeContact && String(activeContact.contactUserId) === String(contactUserId)) {
    activeContact.isOnline = !!isOnline;
    updateChatHeader(activeContact);
  }

  updateContactStatusInDOM(contactUserId, !!isOnline);
  renderContacts(getFilteredContacts());
}

function applyOnlineUsers(onlineUsers) {
  const onlineSet = new Set((onlineUsers || []).map(u => String(u)));
  contacts.forEach(contact => {
    contact.isOnline = onlineSet.has(String(contact.contactUserId));
  });
  if (activeContact) {
    activeContact = getContactById(activeContact.contactUserId) || activeContact;
    updateChatHeader(activeContact);
  }
  renderContacts(getFilteredContacts());
}

function renderContacts(list) {

  if (!list)
    return;

  contactsEl.innerHTML = '';

  if (list.length === 0) {
    contactsEl.style.display = 'none';
    sidebarBody.classList.add('empty');
    sidebarEmptyState.style.display = 'flex';
    noContactsMessage.style.display = 'block';
    noContactsMessageText.textContent = contacts.length === 0
      ? 'No contacts yet. Click "Add Contact" to get started!'
      : 'No matching contacts found.';
    return;
  }

  contactsEl.style.display = 'block';
  sidebarBody.classList.remove('empty');
  sidebarEmptyState.style.display = 'none';
  noContactsMessage.style.display = 'none';

  list.forEach(c => {
    const li = document.createElement('li');
    li.className = 'contact';
    li.dataset.id = c.contactUserId;

    const statusDot = document.createElement('div');
    statusDot.className = 'contact-status ' + (c.isOnline ? 'online' : 'offline');

    const avatar = document.createElement('div');
    avatar.className = 'avatar';
    avatar.textContent = c.contactUserName.charAt(0);

    const meta = document.createElement('div');
    meta.className = 'meta';
    const name = document.createElement('div');
    name.className = 'name';
    name.textContent = c.contactUserName;
    const last = document.createElement('div');
    last.className = 'last';
    last.textContent = c.last || '';  // will also render the last message of each contact later.

    meta.appendChild(name);
    meta.appendChild(last);

    li.appendChild(avatar);
    li.appendChild(meta);
    li.appendChild(statusDot);

    // Add badge if there are unread messages
    if (unreadMessages[c.contactUserId] > 0) {
      const badge = document.createElement('div');
      badge.className = 'unread-badge';
      badge.textContent = unreadMessages[c.contactUserId];
      li.appendChild(badge);
    }

    li.addEventListener('click', () => selectContact(c.contactUserId));

    contactsEl.appendChild(li);
  });
  highlightActive();
}

function highlightActive() {
  document.querySelectorAll('.contact').forEach(node => {
    const activeId = activeContact ? String(activeContact.contactUserId ?? activeContact.id ?? '') : '';
    node.classList.toggle('active', node.dataset.id === activeId);
  });
}

function updateChatHeader(contact) {
  const name = contact ? contact.contactUserName : 'Select a contact';
  const isOnline = contact ? !!(contact.isOnline ?? contact.online ?? contact.active ?? false) : false;

  headerNameEl.textContent = name;
  headerStatusEl.textContent = contact ? (isOnline ? 'Online' : 'Offline') : 'Offline';
  headerStatusEl.classList.toggle('online', isOnline);
  headerStatusEl.classList.toggle('offline', !isOnline);
}

async function selectContact(contactUserId) {
  activeContact = contacts.find(contact => String(contact.contactUserId) === String(contactUserId));

  if (!activeContact) {
    console.warn('selectContact: contact not found', contactUserId);
    return;
  }

  unreadMessages[contactUserId] = 0;
  updateChatHeader(activeContact);

  if (!messages[contactUserId]) {
    await fetchMessages(contactUserId);
  }

  renderMessages(messages[contactUserId]);
  highlightActive();
  updateContactBadge(contactUserId);

  // activeContact.isOnline = true;
  // updateChatHeader(activeContact);
}

function updateContactBadge(contactUserId) {
  const contactElement = document.querySelector(`.contact[data-id="${contactUserId}"]`);
  if (!contactElement) return;

  const badge = contactElement.querySelector('.unread-badge');
  const unreadCount = unreadMessages[contactUserId] || 0;

  if (unreadCount > 0) {
    if (!badge) {
      const newBadge = document.createElement('div');
      newBadge.className = 'unread-badge';
      newBadge.textContent = unreadCount;
      contactElement.appendChild(newBadge);
    } else {
      badge.textContent = unreadCount;
    }
  } else {
    if (badge) {
      badge.remove();
    }
  }
}

function formatTimestamp(value) {
  if (!value) return '';
  const date = new Date(Number(value));
  if (Number.isNaN(date.getTime())) return '';

  const time = date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  const now = new Date();
  const isSameDay = date.getFullYear() === now.getFullYear()
    && date.getMonth() === now.getMonth()
    && date.getDate() === now.getDate();

  if (isSameDay) {
    return time;
  }

  const dateOptions = {
    month: 'short',
    day: 'numeric',
    year: date.getFullYear() !== now.getFullYear() ? 'numeric' : undefined
  };
  const dateString = date.toLocaleDateString([], dateOptions);
  return `${dateString} ${time}`;
}

function renderMessages(msgList) {
  const msgs = Array.isArray(msgList) ? msgList : [];

  messagesEl.classList.remove('empty');
  messagesEl.querySelectorAll('.msg').forEach(node => node.remove());

  if (msgs.length === 0) {
    noConversationMessage.style.display = 'flex';
    messagesEl.classList.add('empty');
    return;
  }

  noConversationMessage.style.display = 'none';

  msgs.forEach(m => {
    const div = document.createElement('div');
    div.className = 'msg ' + (String(m.senderId) === String(currentUserId) ? 'sent' : 'received');

    const text = document.createElement('div');
    text.className = 'msg-text';
    text.textContent = m.content;
    div.appendChild(text);

    const time = document.createElement('div');
    time.className = 'msg-time';
    time.textContent = formatTimestamp(m.timestamp || m.dateTime);
    div.appendChild(time);

    messagesEl.appendChild(div);
  });
  messagesEl.scrollTop = messagesEl.scrollHeight;
}

async function fetchMessages(contactUserId) {


  const response = await fetch(`${baseUrl}/message/${contactUserId}`, {
    method: "GET",
    headers: {
      "Authorization": `Bearer ${jwtToken}`
    }
  });

  if (response.status != 200) {
    console.error('fetchMessages failed with status', response.status);
    return [];
  }

  const body = await response.json();
  loadMessages(contactUserId, body);
  return body;
}

function loadMessages(contactUserId, contactMessages) {
  messages[contactUserId] = Array.isArray(contactMessages)
    ? contactMessages.map(m => ({
        senderId: m.senderId,
        receiverId: m.receiverId,
        content: m.content,
        timestamp: m.timestamp ?? m.dateTime ?? null,
        dateTime: m.dateTime ?? m.timestamp ?? null
      }))
    : [];

  const contact = getContactById(contactUserId);
  const conversation = messages[contactUserId];
  if (contact && conversation.length > 0) {
    contact.last = conversation[conversation.length - 1].content;
    renderContacts(getFilteredContacts());
  }
}

async function preloadLastMessagesForContacts() {
  contacts.forEach(contact => {
    if (!messages[contact.contactUserId]) {
      fetchMessages(contact.contactUserId).catch(() => {});
    }
  });
}

function sendMessage(msg) {
  if (!webSocket)
    return;

  webSocket.send(JSON.stringify(msg));
}

async function setCurrentUserId() {
  const response = await fetch(`${baseUrl}/user`, {
    headers: {
      "Authorization": `Bearer ${jwtToken}`
    }
  });


  if (response.status == 302) {
    const body = await response.json();
    currentUserId = body.id ?? body.currentUserId ?? body.userId;
  }
  else {
    redirectToLogin(response.status);
  }
}

async function initializeApp() {
  const token = localStorage.getItem('jwtToken');


  if (!token) {
    redirectToLogin(401);
    return;
  }

  jwtToken = token;

  await setCurrentUserId();
  if (!currentUserId) {
    return;
  }

  await fetchContacts();
  connectToWebSocket();
}

function connectToWebSocket() {
  webSocket = new WebSocket(`ws://localhost:8080/websocket?id=${currentUserId}`);


  webSocket.onmessage = (event) => {
    let msg;
    try {
      msg = JSON.parse(event.data);
    } catch (err) {
      console.error('Invalid websocket message:', err);
      return;
    }

    if (msg.payloadType === 'ACTIVE_STATUS') {

      const contactUserId = msg.contactUserId;
      setContactOnlineStatus(contactUserId, normalizeActiveStatus(msg.activeStatus));
      return;
    }

    if (msg.payloadType === 'ACTIVE_USERS') {
      applyOnlineUsers(msg.onlineUsers || []);
      return;
    }

    // Normalize message fields
    const senderId = msg.messageDto?.senderId;
    const receiverId = msg.messageDto?.receiverId;
    const content = msg.messageDto?.content;
    const timestamp = msg.messageDto?.timestamp ?? msg.messageDto?.dateTime;

    const convId = String(senderId) === String(currentUserId) ? receiverId : senderId;
    messages[convId] = messages[convId] || [];
    messages[convId].push({ senderId, receiverId, content, timestamp, dateTime: timestamp });

    const contact = getContactById(convId);
    if (contact) {
      contact.last = content;
      renderContacts(getFilteredContacts());
    }

    if (activeContact && String(activeContact.contactUserId) === String(convId)) {
      renderMessages(messages[convId]);
    } else {
      unreadMessages[convId] = (unreadMessages[convId] || 0) + 1;
      updateContactBadge(convId);
    }
  }
}


searchInput.addEventListener('input', () => {
  renderContacts(getFilteredContacts());
});

sendBtn.addEventListener('click', (e) => {
  e.preventDefault();
  if (!activeContact) {
    alert('please select a contact first.');
    return;
  }

  const text = messageInput.value.trim();
  if (!text)
    return;

  const now = Date.now();
  const msg = {
    senderId: currentUserId,
    receiverId: activeContact.contactUserId,
    content: text,
    timestamp: now,
    dateTime: now
  };

  messages[activeContact.contactUserId] = messages[activeContact.contactUserId] || [];
  messages[activeContact.contactUserId].push(msg);

  activeContact.last = msg.content;
  renderContacts(getFilteredContacts());

  sendMessage(msg);
  renderMessages(messages[activeContact.contactUserId]);
});

function showContactError(message) {
  contactModalError.textContent = message;
  contactModalError.style.display = 'block';
}

function clearContactError() {
  contactModalError.textContent = '';
  contactModalError.style.display = 'none';
}

function openContactModal() {
  clearContactError();
  contactModal.classList.add('open');
  contactModal.setAttribute('aria-hidden', 'false');
  contactEmailInput.focus();
}

function closeContactModalFn() {
  contactModal.classList.remove('open');
  contactModal.setAttribute('aria-hidden', 'true');
  clearContactError();
  addContactForm.reset();
}

addContactButton.addEventListener('click', openContactModal);
closeContactModal.addEventListener('click', closeContactModalFn);
cancelContactButton.addEventListener('click', closeContactModalFn);
contactModal.addEventListener('click', (e) => {
  if (e.target === contactModal) {
    closeContactModalFn();
  }
});

addContactForm.addEventListener('submit', (e) => {
  e.preventDefault();
  (async () => {
    const email = contactEmailInput.value.trim();
    if (!email) return;

    const submitBtn = addContactForm.querySelector('button[type="submit"]');
    if (submitBtn) submitBtn.disabled = true;

    try {
      const res = await fetch(`${baseUrl}/contacts/add`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${jwtToken}`
        },
        body: JSON.stringify({
          contactUserEmail: email,
        })
      });

      if (!res.ok) {
        if (res.status == 404) {
          showContactError('Account not found!');
          return;
        }
        if (res.status == 409) {
          const body = await res.json();
          closeContactModalFn();
          selectContact(body.contactUserId);
        }
        return;
      }

      // refresh contacts list
      await fetchContacts();
      closeContactModalFn();
    } catch (err) {
      console.error('Error adding contact', err);
      showContactError('Network error adding contact');
    } finally {
      if (submitBtn) submitBtn.disabled = false;
    }
  })();
});

initializeApp();