
import { mount } from '@vue/test-utils';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import App from '../../App.vue';

// It is often better to stub child components to keep integration tests focused.
const LoginStub = {
  name: 'Login', // The name must match the component's registered name or the import alias in the parent.
  template: `
    <div class="login-view">
      <button @click="$emit('login-successful')">Login</button>
    </div>
  `,
};

const LobbyStub = {
  name: 'LobbyView',
  template: '<div class="lobby-view">Lobby</div>',
};

describe('Login to Lobby Integration', () => {
  let wrapper;

  beforeEach(() => {
    wrapper = mount(App, {
      global: {
        stubs: {
          // Use the component's import name in the parent (`App.vue`) as the key.
          Login: LoginStub,
          LobbyView: LobbyStub,
          AdminLayout: true,
          SpielView: true,
          HilfeView: true,
          HighscoreView: true,
        },
      },
    });
  });

  afterEach(() => {
    if (wrapper) {
      wrapper.unmount();
    }
  });

  it('transitions from LoginView to LobbyView on successful login', async () => {
    // Initial state: App should render the Login stub
    expect(wrapper.find('.login-view').exists()).toBe(true);
    expect(wrapper.find('.lobby-view').exists()).toBe(false);

    // Simulate the login event from the stub
    await wrapper.find('.login-view button').trigger('click');
    await wrapper.vm.$nextTick();

    // Final state: App should now render the LobbyView stub
    expect(wrapper.find('.login-view').exists()).toBe(false);
    expect(wrapper.find('.lobby-view').exists()).toBe(true);
  });
});
