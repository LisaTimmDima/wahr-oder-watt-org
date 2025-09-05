// ==================================================================================
// Verantwortlichkeiten:
// - Lisa: Integrationstest für die Login zu Lobby-Ansicht. 
// ==================================================================================

/**
 * @file Diese Datei enthält einen Integrationstest für den Anmeldevorgang.
 * Es wird überprüft, ob ein Benutzer nach einem erfolgreichen Anmeldeereignis korrekt
 * von der Anmeldeansicht zur Lobbyansicht weitergeleitet wird.
 */

import { mount } from '@vue/test-utils';
import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import App from '../../App.vue';

/**
 * @constant {object} LoginStub
 * @description Eine Stub-Komponente für die Login-Ansicht.
 * Sie simuliert die Anmeldeansicht, indem sie eine Vorlage mit einer Anmelde-Schaltfläche bereitstellt,
 * die bei einem Klick ein 'login-successful'-Ereignis auslöst. Dies hilft, die App-Komponente
 * von der eigentlichen Implementierung der Login-Ansicht zu isolieren.
 */
const LoginStub = {
  name: 'Login', // Der Name muss mit dem registrierten Namen der Komponente übereinstimmen.
  template: `
    <div class="login-view">
      <button @click="$emit('login-successful')">Login</button>
    </div>
  `,
};

/**
 * @constant {object} LobbyStub
 * @description Eine Stub-Komponente für die LobbyView.
 * Sie stellt eine einfache Vorlage bereit, um zu bestätigen, dass die Lobby-Ansicht gerendert wurde.
 */
const LobbyStub = {
  name: 'LobbyView',
  template: '<div class="lobby-view">Lobby</div>',
};

/**
 * @description Test-Suite für die Integration zwischen der Login- und der Lobby-Ansicht.
 * Sie testet den gesamten Anwendungsablauf, wenn sich ein Benutzer anmeldet.
 */
describe('Login to Lobby Integration', () => {
  /**
   * @type {import('@vue/test-utils').VueWrapper}
   * @description Der Wrapper für die gemountete App-Komponente.
   * Er wird verwendet, um mit der Komponenteninstanz zu interagieren und Überprüfungen durchzuführen.
   */
  let wrapper;

  /**
   * @function beforeEach
   * @description Diese Funktion wird vor jedem Testfall in dieser Suite ausgeführt.
   * Sie mountet die Haupt-App-Komponente mit Stub-Komponenten für die Kind-Komponenten,
   * um eine kontrollierte Testumgebung zu schaffen. Stubs werden verwendet, um tiefes Rendern zu verhindern
   * und den Test auf die Interaktion zwischen den Komponenten zu konzentrieren.
   */
  beforeEach(() => {
    wrapper = mount(App, {
      global: {
        stubs: {
          // Stubbing der Kind-Komponenten, um den Fokus des Tests zu isolieren.
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

  /**
   * @function afterEach
   * @description Diese Funktion wird nach jedem Testfall ausgeführt.
   * Sie unmountet die Komponente, um das DOM zu bereinigen und Seiteneffekte zwischen den Tests zu verhindern.
   */
  afterEach(() => {
    if (wrapper) {
      wrapper.unmount();
    }
  });

  /**
   * @function it
   * @description Testfall: Überprüft den Übergang von der LoginView zur LobbyView bei erfolgreicher Anmeldung.
   * @param {string} description - Die Beschreibung des Testfalls.
   * @param {function} testFunction - Die asynchrone Funktion, die die Testlogik enthält.
   */
  it('transitions from LoginView to LobbyView on successful login', async () => {
    // 1. Überprüfung des Ausgangszustands:
    // Sicherstellen, dass die Login-Ansicht gerendert wird und die Lobby-Ansicht nicht.
    expect(wrapper.find('.login-view').exists()).toBe(true);
    expect(wrapper.find('.lobby-view').exists()).toBe(false);

    // 2. Aktionssimulation:
    // Simulieren eines Klicks auf die Anmelde-Schaltfläche im LoginStub.
    // Dies sollte das 'login-successful'-Ereignis auslösen.
    await wrapper.find('.login-view button').trigger('click');
    // Warten, bis Vue die DOM-Aktualisierungen nach dem Ereignis verarbeitet hat.
    await wrapper.vm.$nextTick();

    // 3. Überprüfung des Endzustands:
    // Sicherstellen, dass die Login-Ansicht entfernt wurde und die Lobby-Ansicht jetzt gerendert wird.
    expect(wrapper.find('.login-view').exists()).toBe(false);
    expect(wrapper.find('.lobby-view').exists()).toBe(true);
  });
});
