console.log("Starting...");
const locale = Intl.DateTimeFormat().resolvedOptions().locale.split("-")
const properties=  [
    `user.country=${locale[0]}`,
    `user.language=${locale[1] || ''}`
];

document.addEventListener("cheerpj-ready", async function() {
    const iso = await window.iso639;
    const examples = document.getElementById("input-examples")
    const iterator = await (await iso.stream()).iterator();
    while (await iterator.hasNext()) {
        const lang = await iterator.next();
        const option = document.createElement("option");
        option.value = await lang.code();
        option.textContent = await lang.refName();
        examples.appendChild(option);
    }
    }
);

let setupPromise = null;

window.setup = async function setup() {
    if (setupPromise === null) {
        setupPromise = (async () => {
            if (window.iso639 === undefined) {
                function showPreloadProgress(preloadDone, preloadTotal) {
                    const percentage = Math.round((preloadDone * 100) / preloadTotal);
                    console.log(percentage + "%");
                }

                await cheerpjInit({
                    enableDebug: false,
                    version: 17,
                    javaProperties: properties,
                    // cjGetRuntimeResources();
                    //preloadResources:{"/lt/17/lib/modules":[0,131072,1179648,2097152,2228224,3801088,3932160,4063232,4194304,4587520,4849664,5111808,5636096,5898240,6029312,6291456,6815744,7077888,7864320,7995392,9961472,10092544,37486592,37617664,38010880,38141952],"/lt/etc/users":[0,131072],"/lt/etc/localtime":[],"/lt/17/jre/lib/cheerpj-handlers.jar":[0,131072],"/lt/17/jre/lib/cheerpj-awt.jar":[0,131072],"/lt/17/jre/lib/cheerpj-jsobject.jar":[0,131072]},
                    preloadProgress: showPreloadProgress
                })

                const version = "4.2-SNAPSHOT";
                const prefix = document.location.pathname.startsWith("/i18n-iso-639/") ?
                    "/app/i18n-iso-639/resources/" :
                    "/app/resources/";
                const classpath = `${prefix}i18n-iso-639-${version}.jar`;
                console.log("Classpath:", classpath);
                const cj = await cheerpjRunLibrary(classpath);
                window.iso639 = await cj.org.meeuw.i18n.languages.ISO_639;
                document.dispatchEvent(new Event("cheerpj-ready"));
                document.getElementById("loading").style.display = "none";
                document.getElementById("output_table").style.display = "table";
                return window.iso639;
            }
        })();
    }
    return setupPromise;
}

window.setup();

// helper to set DOM text safely. Accepts a supplier function.
async function setText(id, supplier) {
    const el = document.getElementById(id);
    try {
        let val=  await supplier();
        el.innerHTML = val == null ? '-' : val;
    } catch (e) {
        //console.log(id, e);
        el.textContent = "-";
    }
}

document.getElementById("text_input")
    .addEventListener("keyup",
        async (event) => {
            try {
                const iso = await window.iso639;
                const value = event.target.value;
                const lang = await (await iso.get(value)).orElse(null);
                if (lang) {
                    // simple fields: pass suppliers so setText will handle awaiting and errors
                    await setText("toString", () => lang.toString());

                    await setText("code", () => lang.code());
                    await setText("type", async () => (await lang.languageType()).toString());

                    // scope may be null or an enum
                    await setText("scope", async () => {
                        const s = await lang.scope();
                        return s ? (await (await s).toString()) : null;
                    });

                    // parts may be absent - supplier returns null to let setText show '-'
                    await setText("part1", () => lang.part1());
                    await setText("part2t", () => lang.part2T());
                    await setText("part2b", () => lang.part2B());
                    await setText("part3", () => lang.part3());

                    // names: collect nameRecords (if available) into a comma-separated string
                    await setText("names", async () => {
                        const nrs = await lang.nameRecords();
                        const names = [];
                        const it = await nrs.iterator();
                        while (await it.hasNext()) {
                            const nr = await it.next();
                            names.push(await nr.toString());
                        }
                        return names.length ? names.join(', ') : null;
                    });

                    // class name -> link to javadoc.io
                    await setText("clazz", async () => {
                        const clazz = await lang.getClass();
                        const pakcage = await (await clazz.getPackage()).getName();
                        const clazzName = await clazz.getName();
                        const version = "latest";
                        const group = "org.meeuw.i18n";
                        const artifact = "i18n-iso-639";
                        const path = clazzName.replace(/\./g, '/');
                        const url = `https://www.javadoc.io/doc/${group}/${artifact}/${version}/${pakcage}/${path}.html`;
                        return `<a href="${url}" target="javadoc">${clazzName}</a>`;
                     });


                    await setText("macro", async () => {
                        const macro = await lang.macroLanguages();
                        const names = [];
                        const it = await macro.iterator();
                        while (await it.hasNext()) {
                            const nr = await it.next();
                            const code = await nr.code();
                            const string = await nr.toString();
                            const link = `<a href="?lang=${code}">${string}</a>`;
                            names.push(link);
                        }
                        return names.length ? names.join(', ') : null;
                    });

                    await setText("individual", async () => {
                        const macro = await lang.individualLanguages();
                        const names = [];
                        const it = await macro.iterator();
                        while (await it.hasNext()) {
                            const nr = await it.next();
                            const code = await nr.code();
                            const string = await nr.toString();
                            const link = `<a href="?lang=${code}">${string}</a>`;
                            names.push(link);
                        }
                        return names.length ? names.join(', ') : null;
                    });

                    await setText("uri", async () => {
                        const uris = [];
                        try {
                            const uri = await (await lang.uri()).toString();
                            uris.push(`<a target="ext" href="${uri}">${uri}</a>`)
                        } catch (e) { }
                        try {
                            const part3 = await lang.part3();
                            const uri = `https://www.ethnologue.com/language/${part3}`;
                            uris.push(`<a target="ext" href="${uri}">Ethnologue</a>`)
                        } catch (e) { }
                        return uris.length ? uris.join(', ') : null;
                    });
                    const url = new URL(window.location.href);
                    url.searchParams.set('lang', value);
                    history.pushState(null, '', url.toString());
                } else {
                    // clear table cells using setText with plain values
                    // loop over all table cells that have an id and clear them
                    document.getElementById("toString").textContent = "-";
                    const tds = document.querySelectorAll('#output_table tbody td[id]');
                    for (const td of tds) {
                        td.textContent = "-";
                    }
                }
            } catch (error) {
                console.log(error);
            }
        }
    );

// If a `lang` query parameter is present (e.g. ?lang=nl), fill the input and trigger the keyup handler
(function fillInputFromQuery() {
    try {
        const params = new URLSearchParams(window.location.search || '');
        const q = params.get('lang');
        if (q) {
            const input = document.getElementById('text_input');
            if (input) {
                input.value = q;
                // Dispatch a keyup event so the same handler runs
                const ev = new Event('keyup', { bubbles: true });
                input.dispatchEvent(ev);
            }
        }
    } catch (e) {
        console.log('fillInputFromQuery error', e);
    }
})();

// end of file
