try {
    config.entry.main.push("../../../../src/main/libraries/prismjs/prismjs.js")
    config.entry.main.push("../../../../src/main/libraries/prismjs/prismjs.css")
} catch {
    console.error("Unable to add prism")
}
