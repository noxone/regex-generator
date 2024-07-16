try {
    config.entry.main.push("../../../../src/libraries/prismjs/prismjs.js")
    config.entry.main.push("../../../../src/libraries/prismjs/prismjs.css")
} catch {
    console.error("Unable to add prism")
}
