console.log('loading "lang"...');

// TODO don't have a function but only an object be returned
export default {
    getTexts() {
        return texts;
    }
}

const specialPasswordCharacters = '!"§$%&/()=?+*#\'<>,;.:-_@[]';

const texts = {

    global: {
        specialPasswordCharacters: specialPasswordCharacters,
    },

    page: {

        login: {
            h1: 'Login',
            passwordForget: {
                h1: 'Login recovery',
                text: 'Enter the email given on to your account. We will send you a recovery link.'
            },
            link: {
                register: 'register new account',
                forgot_password: 'forgot password'
            }
        },

        register: {
            h1: 'Register',
            confirmation: 'You sent the account creation request.<br/>' +
                'Check your email for the confirmation code and enter it on the login page.',
        }
    },

    form: {

        login: {
            login: 'Login',
            login_placeholder: 'Login',
            password_placeholder: 'Password',
            email_placeholder: 'Email',

            email: 'Email'
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
            password_description: 'Do have at least 6 chars, at least one lower- and one uppercase letter, one number and one special char (' + specialPasswordCharacters + ') in there.',
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



