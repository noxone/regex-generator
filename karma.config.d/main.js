;(function(config) { // just IIFE to protect local variabled
    const path = require("path") // native Node.JS module
    config.reporters.push("coverage-istanbul")
    config.plugins.push("karma-coverage-istanbul-reporter")
    config.webpack.module.rules.push(
        {
            test: /\.js$/,
            use: {loader: 'istanbul-instrumenter-loader'},
            include: [path.resolve(__dirname, '../module-name/kotlin/')] // here is necessary to use module-name in `build/js/packages`
        }
    )
    config.coverageIstanbulReporter = {
        reports: ["html", "text", "lcov", "json", "cobertura"]
    }
}(config));