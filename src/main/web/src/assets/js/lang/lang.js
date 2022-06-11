import lang_en from '@/assets/js/lang/lang.en.js';
import lang_de from '@/assets/js/lang/lang.de.js';

const languages = {
    en: lang_en,
    de: lang_de
};

let active = 'en';

/**
 * Sets the active language
 * @param language
 */
let lang = {

    getLanguages() {
        return Object.keys(languages);
    },

    getLanguage() {
        return active;
    },

    set(language) {

        if (typeof languages[language] == 'undefined') {
            throw new Error('unknown language: ' + language);
        }

        // remove all keys which are object or string
        for (const key in this) {

            if (typeof this[key] == 'function') {
                continue;
            }

            this[key] = undefined;
        }

        lang = {...lang, ...languages[language]};

        active = language;
        localStorage.setItem('lang', active);

        console.log('active language', active);
        return this;
    },

    /**
     *
     * @param key
     * @param replacements either { key:string, value } or array
     * @returns {null}
     */
    get(key, ...replacements) {

        if (typeof key != 'string') {
            console.warn('key is unhandlable, return null' + key);
            key = '' + key;
        }

        let langs = this;
        let result = null;

        for (const part of key.split('.')) {
            result = langs = langs[part];
            if (typeof result == 'undefined') {
                break;
            }
        }

        if (typeof result != 'string') {
            return 'TODO:' + key + (typeof replacements == 'undefined' ? '' : '(replacements=' + replacements + ')');
        } else if (replacements.length == 0) {
            return result;
        }

        let result2 = '';
        let buffer = null;
        let rIdx = 0;
        for (let i = 0; i < result.length; i++) {

            const c = result.charAt(i);

            if (buffer == null && c == '{') {
                buffer = '';
            } else if (buffer != null && c == '}') {

                result2 += (rIdx >= replacements.length ? '?' : replacements[rIdx++]);
                buffer = null;

            } else if (buffer == null) {
                result2 += c;
            }
        }

        //console.log('result2', result2);

        // TODO handle replacements {}
        return result2;
    }
};

const getDottetStrings = function (obj) {

    //console.log('getDottedStrings()', obj);
    if (typeof obj != 'object') {
        throw new Error('given parameter must be an object: ' + obj);
    }

    const result = {};
    const queue = [obj];

    while (queue.length > 0) {

        const obj = queue.splice(0, 1)[0];

        for (const k in obj) {

            const v = obj[k];

            if (typeof v == 'string') {
                result[k] = obj[k];
            } else if (typeof v == 'object') {

                let newObject = {};

                for (const k2 in v) {
                    newObject[k + '.' + k2] = v[k2];
                }

                queue.push(newObject);
            }
        }
    }

    return result;
};

const precheck = function () {

    const langs = {};
    const keys = [];
    for (let languagesKey in languages) {

        const dottedKeyValues = getDottetStrings(languages[languagesKey]);
        langs[languagesKey] = dottedKeyValues;

        for (const key in dottedKeyValues) {
            if (keys.indexOf(key) > -1) {
                continue;
            }
            keys.push(key);
        }
    }

    //console.log('collected '+ keys.length +' keys: ', keys);

    let miss = 0;

    for (const key in langs) {

        const kvs = langs[key];
        keys.forEach(aKey => {

            if (typeof kvs[aKey] == 'string') {
                return;
            }

            miss++;

            console.error('language "' + key + '" misses key "' + aKey + '"');
        });
    }

    if (miss === 0) {
        console.log('OK checking languages');
    } else {
        console.error('FAIL checking languages, missing=' + miss);
    }

    return lang;
};

precheck();

const ls_lang = localStorage.getItem('lang');
if (ls_lang != undefined && typeof languages[ls_lang] != 'undefined') {
    lang.set(ls_lang);
} else {

    let browserLang = (navigator.language || navigator.userLanguage).substr(0, 2).toLowerCase();
    if (typeof languages[browserLang] == 'undefined') {
        browserLang = 'en';
    }

    lang.set(browserLang);
    localStorage.setItem('lang', browserLang);
}


export default lang;
