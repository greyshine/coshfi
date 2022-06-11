export default {

    global: {

        lang: {
            de: 'Deutsch',
            en: 'English',
            it: 'Italiano',
            es: 'Español',
            nl: 'Nederlands'
        },

        logout: 'Logout',

        back: 'zurück'

    },

    page: {

        login: {
            h1: 'Login',
            passwordForget: {
                h1: 'Login Wiederherstellung',
                text: 'Geben Sie Ihre E-Mail ein. Es wird ein recovery-Link zugesendet.'
            },
            link: {
                register: 'Neuen Account registrieren',
                forgot_password: 'Passwort vergessen',
                regular_login: 'Login'
            }
        },

        register: {
            h1: 'Registrierung',
            confirmation: 'Sie haben einen neuen Account angefordert.<br/>' +
                'Es wurde eine E-Mail gesendet mit dem Bestätigungscode für die Login-Seite.',
        },

        account: {
            h1: 'Account \'{login}\'',
            language: 'Sprache',
            language_description: 'Setzen der genutzten Sprache',
            address: 'Adresse',
            address_description: 'Adresse für den geschäftlichen, postalischen Kontakt. Auch werden etwaige Rechnungen so adressiert.',


        }
    },

    form: {

        login: {
            login: 'Login',
            password: 'Passwort',
            confirmationcode: 'Bestätigungscode',
            email: 'E-Mail',

            login_placeholder: 'Login',
            password_placeholder: 'Passwort',
            email_placeholder: 'Email',

            fail: 'Fehler beim Login',
        },

        register: {

            name: 'Enter your legal (company) name:',
            name_description: 'This will be used as a legal termination of your party.',
            name_placeholder: 'name',
            name_error: 'Name must have a length greater 1 and not be too exotic',

            login: 'Enter a login name you want to use in future. You will be able to create one or several stores for your name:',
            login_placeholder: 'login',
            login_error: 'Login does not have at least 2 proper characters, as well not too exotic.',

            password: 'Geben Sie das gewünschte Passwort ein:',
            password_description: 'Do have at least 6 chars, at least one lower- and one uppercase letter, one number and one special char (!"§$%&/()=?+*#\'<>,;.:-_@[]) in there.',
            password_placeholder: 'Passwort',
            password_error: 'Das Passwort entspricht nicht den anforderungen.',

            password2: 'Passwort-Wiederholung',
            password2_placeholder: 'Passwort',

            password2_error: 'Password repeat does not match',

            email: 'Enter your email address:',
            email_placeholder: 'Enter your email',
            email_error: 'Proper email is missing',

            preSubmitInfo: 'We will send you an email with a confirmation code. Click the link or paste the url in order to confirm your registration.<br/>' +
                'After you have confirmed you will be able to fill in further details for fully setting up your running account.'
        }

    }
};