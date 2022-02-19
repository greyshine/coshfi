const _init = function () {

    let hiddenImprint = true;

    const linkImprint = document.getElementById('aImprint');

    linkImprint.addEventListener('click', (event) => {

        console.log('clicked', event);

        hiddenImprint = !hiddenImprint;

        const displayValue = hiddenImprint ? 'none' : 'inline';
        document.getElementById('imprint_content')
            .style.display = displayValue;

    });
};


const script = function () {

    return {

        init() {
            _init();
            return this;
        },

        hw() {
            console.log('hallowelt!');
            return 'hallo welt!'
        }
    };

}();