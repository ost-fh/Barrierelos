module.exports = {
    env: {
        node: true,
        es2021: true
    },
    extends: [
        "eslint:recommended",
        "plugin:@typescript-eslint/strict-type-checked",
        "plugin:@typescript-eslint/stylistic-type-checked",
    ],
    ignorePatterns: ["dist", ".eslintrc.*"],
    parserOptions: {
        ecmaVersion: "latest",
        sourceType: "module",
        project: "./tsconfig.json",
        tsconfigRootDir: __dirname,
    },
    rules: {
        "@typescript-eslint/no-misused-promises": "off"
    },
}
