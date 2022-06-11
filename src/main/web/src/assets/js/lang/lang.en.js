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

        back: 'back'
    },

    page: {

        login: {
            h1: 'Login',
            passwordForget: {
                h1: 'Login recovery',
                text: 'Enter the email given with your account. We will send you a recovery link.'
            },
            link: {
                register: 'register new account',
                forgot_password: 'forgot password',
                regular_login: 'login page'
            }
        },

        register: {
            h1: 'Register',
            confirmation: 'You sent the account creation request.<br/>' +
                'Check your email for the confirmation code and enter it on the login page.',
        },

        account: {
            h1: 'Account \'{login}\'',

            language: 'Language',
            language_description: 'Set your preferred language',

            name: 'Legal name',
            name_description: 'Your company\'s name or your real name if your are a single person in charge.',
            name_placeholder: 'name / company',

            contactPerson: 'Person of contact',
            contactPerson_description: 'The person at your company/institution for contact',
            contactPerson_placeholder: 'person of contact',

            address: 'Address',
            address_description: 'Your legal address to send physical mail to. Also used as your legal billing address.',
            address_placeholder: 'address',

            email: 'E-Mail',
            email_description: 'Your email for contact. Changing it re-requests a new confirmation. Meanwhile the account will not be accessible.',
            email_placeholder: 'e-mail address',

            phone: 'Phone',
            phone_description: 'Phonenumber for direct contact. Use international notation like +1 123 456-7890',
            phone_placeholder: 'phonenumber',

            modal: {

                emailchange: {

                    title: 'Change email address',
                    text: 'Bla\nemail\nblubb'
                }
            }

        }
    },

    form: {

        login: {
            login: 'Login',
            password: 'Password',
            confirmationcode: 'Confirmationcode',
            email: 'Email',

            login_placeholder: 'Login',
            password_placeholder: 'Password',
            confirmationcode_placeholder: 'confirmationcode',
            email_placeholder: 'Email',

            confirmationcode_description: 'The confirmationcode was sent to you by email.',

            fail_global: 'Failed to login with the given credentials.',
            fail_confirmationcode: 'Do enter the confirmationcode.',

            global: {
                confirmationcode_resent: 'A new password was sent to you by the provided email.'
            }
        },

        register: {

            name: 'Enter your legal (company) name:',
            name_description: 'This will be used as a legal termination of your party.',
            name_placeholder: 'name',
            name_error: 'Name must have a length greater 1 and not be too exotic',

            login: 'Enter a login name you want to use in future. You will be able to create one or several stores for your name:',
            login_placeholder: 'login',
            login_error: 'Login does not have at least 2 proper characters, as well not too exotic.',

            password: 'Enter a password',
            password_description: 'Do have at least 6 chars, at least one lower- and one uppercase letter, one number and one special char (!"§$%&/()=?+*#\'<>,;.:-_@[]) in there.',
            password_placeholder: 'password',
            password_error: 'Password does not meet requirement',

            password2: 'Repeat the password',
            password2_placeholder: 'password',

            password2_error: 'Password repeat does not match',

            email: 'Enter your email address:',
            email_placeholder: 'Enter your email',
            email_error: 'Proper email is missing',

            preSubmitInfo: 'We will send you an email with a confirmation code. Click the link or paste the url in order to confirm your registration.<br/>' +
                'After you have confirmed you will be able to fill in further details for fully setting up your running account.'
        }
    }
};