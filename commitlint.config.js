export default {
  extends: ["@commitlint/config-conventional"],
  rules: {
    "scope-enum": [
      2,
      "always",
      ["backend", "frontend", "docker", "docs", "ci", "config"],
    ],
  },
};
