module.exports = {
    color: true,
    extension: ['ts'],
    spec: ['src/__tests__/**/*.test.ts'],
    timeout: false,
    ui: 'bdd',
    "node-option": [
        "import=tsx/esm"
    ]
};