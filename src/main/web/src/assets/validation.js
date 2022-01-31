const getAttValidation = function (htmlElement) {

    if (htmlElement == null) {
        return undefined;
    } else if (htmlElement.attributes == null) {
        return undefined;
    } else if (htmlElement.attributes.length == null) {
        return undefined;
    }

    for (let i = 0, l = htmlElement.attributes.length; i < l; i++) {
        if (htmlElement.attributes[i].name === 'validation') {
            return htmlElement.attributes[i].value;
        }
    }

    return undefined;
};

const getFormValidation = function (htmlElement) {

    if (htmlElement == null) {
        return undefined;
    } else if (htmlElement.tagName == null) {
        return undefined;
    }

    while (htmlElement != null && 'FORM' !== htmlElement.tagName) {
        htmlElement = htmlElement.parentNode;
    }

    return htmlElement == null ? null : getAttValidation(htmlElement);
}

const getFieldElement = function (htmlElement, formName = 'form', fieldName) {

    if (htmlElement == null || fieldName == null) {
        return undefined;
    }

    console.log('getFieldElement', formName, fieldName, htmlElement);
    // up to html
    while (htmlElement != null && htmlElement.nodeName != 'HTML') {
        htmlElement = htmlElement.parentElement;
    }

    if (htmlElement == null) {
        throw new Error('cannot find a root HTML node');
    }

    const q = [htmlElement];
    while (q.length > 0) {

        htmlElement = q.shift();
        //console.log('lst check', htmlElement);

        const validation = getAttValidation(htmlElement);

        if (validation === formName && htmlElement.nodeName == 'FORM') {
            // remove all other in queue
            q.length = 0;

        } else if (validation === fieldName && htmlElement.value != undefined) {
            return htmlElement;
        }

        htmlElement.children.forEach(c => q.unshift(c));
    }

    return undefined;
};

const getFieldValue = function (htmlElement, formName, fieldName) {

    const fieldElement = getFieldElement(htmlElement, formName, fieldName);
    console.log('fieldElement', fieldElement);
    return fieldElement == null ? undefined : fieldElement.value;
};

const _assertString = function (value, allowNull = true) {

    if (value == null && allowNull) {
        return;
    } else if (typeof value != 'string') {
        throw new Error('value is not a string, typeof ' + value + '=' + typeof value);
    }
}

class Validator {

    constructor() {

        this._bus = null;

        this._model = null;
        this._errorClass = 'error';
        this._formState = {};
    }

    set errorClass(errorClass) {
        console.log('errorClass', errorClass);
        this._errorClass = errorClass;
    }

    hw() {
        console.log('validation-hw');
    }

    /**
     *
     *
     *
     * @param vueComponent
     * @param infoModel
     */
    init(vueComponent, infoModel) {

        console.log('init', vueComponent);

        this._bus = vueComponent.$root.bus;

        console.log('infoModel', infoModel);

        if (typeof infoModel !== 'object') {
            throw new Error('infoModel must be an object');
        }

        this._model = infoModel;

        // add @blur on form fields
        const q = [document.children[0]];
        while (q.length > 0) {

            const node = q.shift();
            node.childNodes.forEach(node => q.push(node));

            if (typeof getAttValidation(node) == 'undefined') {
                continue;
            }

            switch (node.nodeName) {
                case 'INPUT':
                case 'TEXTAREA':
                case 'SELECT':
                    node.onblur = event => this.validate(event);
                    break;
                default:
                //console.log('ignore node for onblur handler', node.nodeName, node);
            }
        }
    }

    validate(event) {

        console.log('validate1', event);
        // console.log('event.path', event.path[0]);
        // console.log('event.path.value', typeof event.path[0].value, event.path[0].value);
        // console.log('model', this.model);
        // console.log('validation', this.getValidation( event.path[0] ));

        const htmlNode = event.path[0];

        console.log('htmlNode', htmlNode);

        const validationName = getAttValidation(htmlNode);
        // should break on intention!
        //if ( validationName == null ) { return; }

        const formValidationName = getFormValidation(htmlNode);
        // should break on intention!
        //if ( formValidationName == null ) { return; }

        this._validate(formValidationName, validationName, htmlNode);
    }

    _checkInfo(formValidationName, validationName) {
        try {
            return this._model[formValidationName][validationName].check;
        } catch (error) {
            throw new Error('Unknown check info at formValidationName=' + formValidationName + ', validationName=' + validationName + ' (' + error + ')');
        }
    }

    _getFieldValue(formValidationName, validationName) {

        if (typeof formValidationName !== 'string' || formValidationName.trim().length == 0) {
            throw new Error('formValidationName must be a string');
        } else if (typeof validationName !== 'string' || validationName.trim().length == 0) {
            throw new Error('validationName must be a string');
        }

        const q = [document.children[0]];
        while (q.length > 0) {

            const node = q.shift();

            if (node.nodeName != 'FORM' &&
                formValidationName == getFormValidation(node) &&
                validationName == getAttValidation(node)) {
                return node.value;
            }

            node.childNodes.forEach(node => q.push(node));
        }

        return undefined;
    }

    _validate(formValidationName, validationName, htmlNode) {

        //console.log('validate', formValidationName, validationName, htmlNode.value);

        if (typeof htmlNode != 'object' || typeof htmlNode.value == 'undefined') {
            console.error('expected htmlNode to be a node with value', htmlNode);
            return;
        }

        const check = this._checkInfo(formValidationName, validationName);
        //console.log('check?', typeof check, check, formValidationName, validationName);
        const isCheckFailure = typeof check == 'function' ? check(htmlNode.value, htmlNode) != true : !check.test(htmlNode.value);

        // console.log('check', check, 'checkFailure='+isCheckFailure, 'value='+htmlNode.value);

        if (isCheckFailure) {

            htmlNode.classList.add(this._errorClass);
            this._bus.$emit('messages', 'add', this._model[formValidationName][validationName].message, true);

            return false;

        } else {

            htmlNode.classList.remove(this._errorClass);
            this._bus.$emit('messages', 'delete', this._model[formValidationName][validationName].message);

            return true;
        }
    }

    validateSubmit(event) {

        let errors = false;

        let htmlNode = event.path[0];
        const formValidationName = getFormValidation(htmlNode) || 'form';
        console.log('formValidationName', formValidationName);

        while (htmlNode != null && htmlNode.nodeName !== 'FORM') {
            htmlNode = htmlNode.parentNode;
        }

        const validationName = getAttValidation(htmlNode);

        if (validationName == null) {
            console.error('no form found');
            return;
        }

        console.log('validation', htmlNode);

        const q = [];
        q.push(htmlNode);
        while (q.length > 0) {

            htmlNode = q.shift();
            htmlNode.childNodes.forEach(cn => q.push(cn));

            const validationAttValue = getAttValidation(htmlNode);
            if (typeof validationAttValue == 'undefined') {
                continue;
            } else if (typeof htmlNode.value == 'undefined') {
                continue;
            }

            errors |= !this._validate(formValidationName, validationAttValue, htmlNode);
        }

        return errors;
    }

    checkNotBlank(value) {
        _assertString(value);
        return value != null && value.trim() !== '';
    }

    checkEmail(value) {

        if (value == null) {
            return false;
        }

        _assertString(value);

        return value.toLowerCase().match(/^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/)
    }

    checkEqualField(form, field) {

        return function (value, htmlElement) {

            if (value == null) {
                return false;
            }

            //console.log('check', form, field, value, htmlElement);

            const otherValue = getFieldValue(htmlElement, form, field);
            //console.log('otherValue', otherValue);

            if (otherValue == undefined) {
                return value == null;
            }

            return value === otherValue;
        }
    }
}

export default new Validator();