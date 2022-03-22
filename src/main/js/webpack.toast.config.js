const path = require('path');

module.exports = {
    mode: "production",
    entry: './node_modules/@toast-ui/editor/dist/esm/index.js',
    output: {
        filename: 'toastui-all.min.mjs',
        path: path.resolve(__dirname, '../../../build'),
        libraryTarget: 'module'
    },
    experiments: {
        outputModule: true
    }
};