const path = require('path');

module.exports = {
    mode: "production",
    entry: './build/webapp/worker.mjs',
    output: {
        filename: 'worker.js',
        path: path.resolve(__dirname, '../../../build')
    }
};