const emitter = new EventTarget();
let source = null;

export function initSse(userId) {
  if (source) return;
  const url = `/sse/game?userId=${encodeURIComponent(String(userId || ''))}`;
  source = new EventSource(url);
  source.onmessage = (ev) => {
    try {
      const msg = JSON.parse(ev.data);
      // dispatch event by type and generic message
      emitter.dispatchEvent(new CustomEvent(msg.type, { detail: msg.payload }));
      emitter.dispatchEvent(new CustomEvent('message', { detail: msg }));
    } catch (e) {
      console.error('SSE parse error', e);
    }
  };
  source.onerror = (err) => {
    console.error('SSE error', err);
    emitter.dispatchEvent(new CustomEvent('sse-error', { detail: err }));
  };
}

export function onEvent(type, handler) {
  emitter.addEventListener(type, handler);
}

export function offEvent(type, handler) {
  emitter.removeEventListener(type, handler);
}

export function closeSse() {
  if (!source) return;
  try { source.close(); } catch (e) { /* ignore */ }
  source = null;
}