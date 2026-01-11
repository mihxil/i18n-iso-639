console.log("Starting...");
const locale = Intl.DateTimeFormat().resolvedOptions().locale.split("-")
const properties=  [
    `user.country=${locale[0]}`,
    `user.language=${locale[1] || ''}`
]
console.log("Properties:s", properties);

async function setup() {
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
    window.iso = cj.org.meeuw.i18n.languages.ISO_639;
    document.dispatchEvent(new Event("cheerpj-ready"));

}

setup();

document.addEventListener("cheerpj-ready", async function() {
    const iso = await window.iso;
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

document.getElementById("text_input")
    .addEventListener("keyup",
        async (event) => {
            try {
                const iso = await window.iso;
                const value = event.target.value;
                const lang = await (await iso.get(value)).orElse(null);
                if (lang) {
                    let result = await lang.toString();
                    result += `\ncode: ${await lang.code()}`;
                    result += `\ntype: ${await (await lang.languageType()).toString()}`;
                    result += `\nscope: ${await (await lang.scope()).toString()}`;
                    try {
                        result += `\npart1: ${await lang.part1()}`;
                        result += `\npart2T: ${await lang.part2T()}`;
                        result += `\npart2B: ${await lang.part2B()}`;
                        result += `\npart3: ${await lang.part3()}`;

                    } catch (e) {
                        result += `\nclass: ${await (await lang.getClass()).getName()}`;
                    }

                    document.getElementById("output").textContent = result;
                } else {
                    document.getElementById("output").textContent = "Not found: " + value;
                }
            } catch (error) {
                document.getElementById("output").textContent = error;
            }
        }
    );
