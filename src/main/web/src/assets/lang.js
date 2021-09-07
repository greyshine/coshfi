console.log('loading "lang"...');

const specialPasswordCharacters = '!"§$%&/()=?+*#\'<>,;.:-_@[]|';

const texts = {

    global: {
        specialPasswordCharacters: specialPasswordCharacters,
    },

    page: {

        register: {
            h1: 'Register',
            confirmation: 'You sent the account creation request.<br/>' +
                'Check your email for the confirmation code and enter it on the login page.',
        }
    },

    form: {

        register: {

            name: 'Enter your legal (company) name:',
            name_placeholder: 'Enter your name',
            name_error: 'Name must have a length greater 1 and not be too exotic',

            login: 'Enter a login name you want to user in future. You will be able to create one or several stores for your name:',
            login_placeholder: 'Enter your login',
            login_error: 'Login does not have at least 2 proper characters, as well not too exotic.',

            password: 'Enter a password at least 6 chars. Have an lower- and uppercase letter, a number and at least 1 special char ('+ specialPasswordCharacters +') in there.<br/>Exactly repeat that password in the second field.',
            password_placeholder: 'Enter your password',
            password2_placeholder: 'Repeat your password',
            password_error: 'Password does not meet requirement',
            password2_error: 'Password repeat does not match',

            email: 'Enter your email address:',
            email_placeholder: 'Enter your email',
            email_error: 'Proper email is missing',

            preSubmitInfo: 'We will send you an email with a confirmation code. Click the link or paste the url in order to confirm your registration.<br/>' +
                'After you have confirmed you will be able to fill in further details for fully setting up your running account.'
        }
    }
};



export default {
    getTexts() {
        return texts;
    }
}
