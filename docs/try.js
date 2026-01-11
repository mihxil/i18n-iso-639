console.log("Starting...");
const properties=  [
	`user.timezone=${Intl.DateTimeFormat().resolvedOptions().timeZone}`
]
console.log("Properties:", properties);
let setupPromise = null;

window.setup = async function setup() {
    if (setupPromise === null) {
        setupPromise = (async () => {
            if (window.iso === undefined) {
                function showPreloadProgress(preloadDone, preloadTotal) {
                    const percentage = Math.round((preloadDone * 100) / preloadTotal);
                    console.log(percentage + "%");
                }
                await cheerpjInit({
                    enableDebug: false,
                    version: 17,
                    javaProperties: properties,
                    // cjGetRuntimeResources();
                    preloadResources:{"/lt/17/lib/modules":[0,131072,1179648,2097152,2228224,3801088,3932160,4063232,4194304,4587520,4849664,5111808,5636096,5898240,6029312,6291456,6815744,7077888,7864320,7995392,9961472,10092544,37486592,37617664,38010880,38141952],"/lt/etc/users":[0,131072],"/lt/etc/localtime":[],"/lt/17/jre/lib/cheerpj-handlers.jar":[0,131072],"/lt/17/jre/lib/cheerpj-awt.jar":[0,131072],"/lt/17/jre/lib/cheerpj-jsobject.jar":[0,131072]},
                    preloadProgress: showPreloadProgress
                })

                const prefix = "/app/";
                const classpath = `${prefix}i18n-iso-639-4.2-SNAPSHOT.jar`;
                console.log("Classpath:", classpath);
                const cj = await cheerpjRunLibrary(classpath);
                const clazz = await cj.org.meeuw.i18n.languages.ISO_639;
                window.iso = clazz;
                console.log("Parser loaded", window.iso);
            }
            return window.iso;
        })();
    }
    return setupPromise;
}
window.setup();

document.getElementById("text_input").addEventListener("keyup", async (event) => {
    const iso = await window.setup();
    const value = event.target.value;
    const lang = await (await iso.get(value)).orElse(null);
    if (lang) {
        document.getElementById("output").textContent = lang ? await lang.toString() : "Not found";
    } else {
        document.getElementById("output").textContent = "Not found";
    }
});
