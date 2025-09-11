// ==================================================================================
// Verantwortlichkeiten:
// - Lisa: Tests für die LoginView.vue-Komponente.
// - Dima: API-Tests
// ==================================================================================

// 'mount' von '@vue/test-utils' wird verwendet, um Vue-Komponenten in Tests zu rendern.
// 'describe', 'it', 'expect', 'vi', 'beforeEach' sind Kernfunktionen von Vitest für die Strukturierung und Durchführung von Tests.
import { mount } from '@vue/test-utils';
import { describe, it, expect, vi, beforeEach } from 'vitest';

// Importiert die zu testende Vue-Komponente.
import LoginView from '../LoginView.vue'

// --- Mock für localStorage ---
// Da Tests in einer Node.js-Umgebung laufen, ist das 'localStorage'-Objekt des Browsers nicht verfügbar.
// Dieses Mock-Objekt simuliert die Funktionalität von localStorage für die Testumgebung.
const localStorageMock = (() => {
  // 'store' ist ein einfaches JavaScript-Objekt, das die Daten für unser Mock speichert.
  let store = {};
  return {
    /**
     * @param {string} key Der Schlüssel des abzurufenden Elements.
     * @returns {string | null} Der Wert des Elements oder null, wenn nicht gefunden.
     */
    getItem: key => store[key] || null,
    /**
     * @param {string} key Der Schlüssel des zu setzenden Elements.
     * @param {string} value Der Wert des zu setzenden Elements.
     */
    setItem: (key, value) => {
      store[key] = value.toString();
    },
    /**
     * Leert den gesamten Speicher.
     */
    clear: () => {
      store = {};
    },
    /**
     * @param {string} key Der Schlüssel des zu entfernenden Elements.
     */
    removeItem: key => {
      delete store[key];
    }
  };
})();

// Überschreibt das globale 'localStorage'-Objekt mit unserem Mock.
// Dadurch wird sichergestellt, dass die Komponente innerhalb des Tests unser simuliertes localStorage verwendet.
Object.defineProperty(window, 'localStorage', { value: localStorageMock });

// --- Test-Suite für LoginView.vue ---
// 'describe' gruppiert zusammengehörige Tests für eine bessere Organisation.
describe('LoginView.vue', () => {
  
  // 'wrapper' wird die gemountete Vue-Komponente enthalten.
  // Sie wird außerhalb von 'beforeEach' deklariert, um in allen Tests der Suite verfügbar zu sein.
  let wrapper;

  /**
   * Hilfsfunktion, um auf den nächsten DOM-Aktualisierungszyklus von Vue zu warten.
   * Dies ist notwendig, da Vue DOM-Änderungen asynchron durchführt.
   * @returns {Promise<void>} Ein Promise, das aufgelöst wird, nachdem die DOM-Aktualisierung erfolgt ist.
   */
  const nextTick = async () => {
    await new Promise(resolve => setTimeout(resolve, 0));
  };

  // 'beforeEach' ist ein Hook, der vor jedem einzelnen Test ('it'-Block) in dieser Suite ausgeführt wird.
  // Er stellt sicher, dass jeder Test mit einem sauberen und definierten Zustand beginnt.
  beforeEach(() => {
    // Setzt alle Mocks von 'vi' zurück, um Testinterferenzen zu vermeiden.
    vi.restoreAllMocks();
    // Leert unser localStorage-Mock vor jedem Test.
    localStorage.clear();
    // Mockt die globale 'fetch'-Funktion, damit keine echten Netzwerk-Anfragen gesendet werden.
    global.fetch = vi.fn();

    // Mountet die LoginView-Komponente und hängt sie an den DOM an.
    // 'attachTo' stellt sicher, dass die Komponente vollständig im JSDOM gerendert wird, was für Interaktionstests wichtig ist.
    wrapper = mount(LoginView, {
      attachTo: document.body
    });
  });

  // --- Test für den initialen Zustand und das Rendering ---
  describe('Initial State & Rendering', () => {
    // Testet, ob das Benutzer-Login-Formular standardmäßig korrekt angezeigt wird.
    it('renders the user login form by default', () => {
      expect(wrapper.find('h1').text()).toContain('Wahr oder Watt?');
      expect(wrapper.find('input[type="email"]').exists()).toBe(true);
      expect(wrapper.find('input[type="password"]').exists()).toBe(true);
      expect(wrapper.find('button[type="submit"]').text()).toBe('Anmelden');
    });
  });

  // --- Tests für den Wechsel zwischen den verschiedenen Modi (Login, Registrierung) ---
  describe('Mode Switching', () => {
    // Testet den Wechsel zum Admin-Login-Formular.
    it('switches to admin login form', async () => {
      await wrapper.findAll('button[type="button"]').find(b => b.text() === 'Admin-Login').trigger('click');
      await nextTick(); // Warten auf DOM-Update nach dem Klick.
      expect(wrapper.find('p.text-center').text()).toContain('Admin-Login');
      expect(wrapper.find('input[type="text"]').exists()).toBe(true); // Benutzername-Input
      expect(wrapper.find('input[type="password"]').exists()).toBe(true);
    });

    // Testet den Wechsel zum Registrierungsformular.
    it('switches to registration form', async () => {
      await wrapper.findAll('button[type="button"]').find(b => b.text() === 'Hier registrieren').trigger('click');
      await nextTick();
      expect(wrapper.find('h1').text()).toContain('Erstellen Sie Ihr Konto');
      expect(wrapper.find('input#reg-email').exists()).toBe(true);
      expect(wrapper.find('input#reg-username').exists()).toBe(true);
      expect(wrapper.find('input#reg-password').exists()).toBe(true);
    });

    // Testet den Wechsel zum "Passwort vergessen"-Formular.
    it('switches to forgot password form', async () => {
      await wrapper.findAll('button[type="button"]').find(b => b.text() === 'Passwort vergessen?').trigger('click');
      await nextTick();
      expect(wrapper.find('h1').text()).toContain('Passwort vergessen?');
      expect(wrapper.find('input#forgot-email').exists()).toBe(true);
    });
  });

  // --- Tests für den Benutzer-Login-Prozess ---
  describe('User Login', () => {
    // Testet den erfolgreichen Login-Fall.
    it('emits login-successful and stores token on successful login', async () => {
      // Definiert ein simuliertes JWT-Token für den erfolgreichen Login.
      const mockToken = 'fake-jwt-token';
      // Simuliert eine erfolgreiche 'fetch'-Antwort vom Server.
      global.fetch.mockResolvedValue({
        ok: true,
        text: () => Promise.resolve(mockToken),
      });

      // Füllt das Formular aus und sendet es ab.
      await wrapper.find('input[type="email"]').setValue('test@user.com');
      await wrapper.find('input[type="password"]').setValue('password');
      await wrapper.find('form').trigger('submit.prevent');
      await nextTick(); // Warten auf die Verarbeitung der asynchronen Login-Logik.

      // Überprüft, ob 'fetch' mit den korrekten Daten aufgerufen wurde.
      expect(global.fetch).toHaveBeenCalledWith('/api/auth/login', expect.any(Object));
      // Überprüft, ob das 'login-successful'-Event ausgelöst wurde.
      expect(wrapper.emitted('login-successful')).toBeTruthy();
      // Überprüft, ob das Token korrekt im localStorage gespeichert wurde.
      expect(localStorage.getItem('jwt')).toBe(mockToken);
    });

    // Testet den fehlgeschlagenen Login-Fall.
    it('shows an error message on failed login', async () => {
      // Simuliert eine fehlgeschlagene 'fetch'-Antwort vom Server.
      global.fetch.mockResolvedValue({
        ok: false,
        json: () => Promise.resolve({ message: 'Login fehlgeschlagen' }),
      });

      // Füllt das Formular mit falschen Daten aus und sendet es ab.
      await wrapper.find('input[type="email"]').setValue('wrong@user.com');
      await wrapper.find('input[type="password"]').setValue('wrong');
      await wrapper.find('form').trigger('submit.prevent');
      await nextTick();

      // Überprüft, ob eine Fehlermeldung angezeigt wird.
      expect(wrapper.find('.bg-red-100').text()).toContain('Login fehlgeschlagen');
      // Überprüft, ob kein Token im localStorage gespeichert wurde.
      expect(localStorage.getItem('jwt')).toBeNull();
    });
  });

  // --- Tests für den Admin-Login-Prozess ---
  describe('Admin Login', () => {
    // Testet den erfolgreichen Admin-Login.
    it('emits admin-login-successful and stores token on successful admin login', async () => {
        // Wechselt zuerst in den Admin-Login-Modus.
        await wrapper.findAll('button[type="button"]').find(b => b.text() === 'Admin-Login').trigger('click');
        await nextTick();
        
        // Definiert ein simuliertes Admin-JWT-Token.
        const mockToken = 'fake-admin-jwt-token';
        // Simuliert eine erfolgreiche 'fetch'-Antwort.
        global.fetch.mockResolvedValue({
            ok: true,
            text: () => Promise.resolve(mockToken),
        });

        // Füllt das Admin-Formular aus und sendet es ab.
        await wrapper.find('input[type="text"]').setValue('admin');
        await wrapper.find('input[type="password"]').setValue('adminpass');
        await wrapper.find('form').trigger('submit.prevent');
        await nextTick();

        // Überprüft, ob der korrekte Admin-Endpunkt aufgerufen wurde.
        expect(global.fetch).toHaveBeenCalledWith('/api/auth/login_admin', expect.any(Object));
        // Überprüft, ob das korrekte Event für den Admin-Login ausgelöst wurde.
        expect(wrapper.emitted('admin-login-successful')).toBeTruthy();
        // Überprüft, ob das Admin-Token gespeichert wurde.
        expect(localStorage.getItem('jwt')).toBe(mockToken);
    });
  });

  // --- Tests für den Registrierungsprozess ---
  describe('Registration', () => {
    // Wechselt vor jedem Test in diesem Block zum Registrierungsformular.
    beforeEach(async () => {
      await wrapper.findAll('button[type="button"]').find(b => b.text() === 'Hier registrieren').trigger('click');
      await nextTick();
    });

    // Testet die Validierung für eine ungültige E-Mail-Adresse.
    it('shows an error if email is invalid', async () => {
      await wrapper.find('input#reg-email').setValue('invalid-email');
      await wrapper.find('form').trigger('submit.prevent');
      await nextTick();
      expect(wrapper.find('.bg-red-100').text()).toContain('Bitte geben Sie eine gültige E-Mail-Adresse ein.');
    });

    // Testet die Validierung für ein zu kurzes Passwort.
    it('shows an error if password is too short', async () => {
        await wrapper.find('input#reg-email').setValue('new@user.com');
        await wrapper.find('input#reg-username').setValue('newuser');
        await wrapper.find('input#reg-password').setValue('123'); // Zu kurz
        await wrapper.find('form').trigger('submit.prevent');
        await nextTick();
        expect(wrapper.find('.bg-red-100').text()).toContain('Das Passwort muss mindestens 8 Zeichen lang sein.');
    });

    // Testet den erfolgreichen Registrierungsfall.
    it('switches to login on successful registration', async () => {
        // Simuliert eine erfolgreiche Registrierung beim Server.
        global.fetch.mockResolvedValue({ ok: true, json: () => Promise.resolve({}) });
        
        // Füllt das Formular mit gültigen Daten aus.
        await wrapper.find('input#reg-email').setValue('new@user.com');
        await wrapper.find('input#reg-username').setValue('newuser');
        await wrapper.find('input#reg-password').setValue('password123');
        await wrapper.find('form').trigger('submit.prevent');
        await nextTick();
        
        // Überprüft, ob die Ansicht nach erfolgreicher Registrierung zurück zum Login wechselt.
        expect(wrapper.find('h1').text()).toContain('Wahr oder Watt?');
    });
  });

  // --- Tests für die "Passwort vergessen" und "Passwort zurücksetzen"-Funktionen ---
  describe('Forgot Password & Password Reset', () => {
    // Testet den Flow, bei dem eine E-Mail zum Zurücksetzen gesendet wird.
    it('switches to email sent confirmation', async () => {
      // Navigiert zum "Passwort vergessen"-Formular.
      await wrapper.findAll('button[type="button"]').find(b => b.text() === 'Passwort vergessen?').trigger('click');
      await nextTick();
      
      // Füllt die E-Mail aus und sendet das Formular ab.
      await wrapper.find('input#forgot-email').setValue('reset@user.com');
      await wrapper.find('form').trigger('submit.prevent');
      await nextTick();
      
      // Überprüft, ob die Bestätigungsseite angezeigt wird.
      expect(wrapper.find('h1').text()).toContain('Überprüfen Sie Ihre E-Mail.');
    });

    // Testet die Validierung für nicht übereinstimmende Passwörter beim Zurücksetzen.
    it('shows an error if passwords do not match on reset', async () => {
        // Simuliert den Wechsel in den Modus zum Zurücksetzen des Passworts.
        // Dies geschieht programmatisch, da der Link aus der E-Mail nicht simuliert werden kann.
        await wrapper.vm.switchMode('reset-password');
        await nextTick();

        // Gibt zwei unterschiedliche Passwörter ein.
        await wrapper.find('input#new-password').setValue('newpass123');
        await wrapper.find('input#confirm-password').setValue('newpass456');
        await wrapper.find('form').trigger('submit.prevent');
        await nextTick();

        // Überprüft, ob die entsprechende Fehlermeldung erscheint.
        expect(wrapper.find('.bg-red-100').text()).toContain('Die Passwörter stimmen nicht überein!');
    });
  });

  // --- Tests für die Barrierefreiheitsfunktionen ---
  describe('Accessibility Features', () => {
    // Testet die Vergrößerung und Verkleinerung der Schriftgröße.
    it('increases and decreases font size', async () => {
        // Speichert die initiale Schriftgröße.
        const initialSize = wrapper.vm.baseFontSize;
        
        // Klickt auf den Button zum Vergrößern.
        const increaseButton = wrapper.findAll('button.px-2.py-1').find(b => b.text() === '+');
        await increaseButton.trigger('click');
        expect(wrapper.vm.baseFontSize).toBe(initialSize + 2);

        // Klickt auf den Button zum Verkleinern.
        const decreaseButton = wrapper.findAll('button.px-2.py-1').find(b => b.text() === '-');
        await decreaseButton.trigger('click');
        expect(wrapper.vm.baseFontSize).toBe(initialSize); // Zurück zum Ausgangswert
    });

    // Testet das Umschalten des Hochkontrastmodus.
    it('toggles high contrast mode', async () => {
        // Überprüft den initialen Zustand.
        expect(wrapper.vm.isHighContrast).toBe(false);

        // Findet und klickt den Kontrast-Button.
        const contrastButton = wrapper.find('button.px-3.py-1');
        await contrastButton.trigger('click');
        await nextTick();

        // Überprüft, ob der Zustand umgeschaltet wurde und die entsprechende CSS-Klasse vorhanden ist.
        expect(wrapper.vm.isHighContrast).toBe(true);
        expect(wrapper.find('.high-contrast').exists()).toBe(true);
    });
  });
});