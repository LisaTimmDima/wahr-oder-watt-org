import { describe, it, expect, vi } from 'vitest';
import { ref } from 'vue';

// Mock setup for Login.vue logic
function createLoginLogic() {
  const mode = ref('user-login');
  const email = ref('');
  const username = ref('');
  const password = ref('');
  const emit = vi.fn();

  function handleLogin() {
    if (mode.value === 'user-login') {
      if (!email.value || !password.value) return;
      emit('login-successful');
    } else {
      if (!username.value || !password.value) return;
      emit('login-successful');
    }
  }

  return { mode, email, username, password, emit, handleLogin };
}

describe('Login Logic', () => {
  it('emits login-successful for user-login with valid credentials', () => {
    const { mode, email, password, emit, handleLogin } = createLoginLogic();
    mode.value = 'user-login';
    email.value = 'test@example.com';
    password.value = 'password123';
    handleLogin();
    expect(emit).toHaveBeenCalledWith('login-successful');
  });

  it('does not emit for user-login with missing credentials', () => {
    const { mode, email, password, emit, handleLogin } = createLoginLogic();
    mode.value = 'user-login';
    email.value = '';
    password.value = '';
    handleLogin();
    expect(emit).not.toHaveBeenCalled();
  });

  it('emits login-successful for admin-login with valid credentials', () => {
    const { mode, username, password, emit, handleLogin } = createLoginLogic();
    mode.value = 'admin-login';
    username.value = 'admin';
    password.value = 'adminpass';
    handleLogin();
    expect(emit).toHaveBeenCalledWith('login-successful');
  });

  it('does not emit for admin-login with missing credentials', () => {
    const { mode, username, password, emit, handleLogin } = createLoginLogic();
    mode.value = 'admin-login';
    username.value = '';
    password.value = '';
    handleLogin();
    expect(emit).not.toHaveBeenCalled();
  });
});
