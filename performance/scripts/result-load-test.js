import http from "k6/http";
import { check } from "k6";

const BASE_URL = __ENV.BASE_URL || "http://localhost:8080/api/v1";
const SESSION_ID = __ENV.SESSION_ID;
const VUS = Number.parseInt(__ENV.VUS || "50", 10);
const DURATION = __ENV.DURATION || "30s";

if (!SESSION_ID) {
  throw new Error("SESSION_ID is required");
}

if (!Number.isInteger(VUS) || VUS < 1) {
  throw new Error("VUS must be a positive integer");
}

export const options = {
  scenarios: {
    result: {
      executor: "constant-vus",
      vus: VUS,
      duration: DURATION,
    },
  },
  thresholds: {
    checks: ["rate>0.99"],
    "http_req_failed{endpoint:result}": ["rate<0.01"],
    "http_req_duration{endpoint:result}": ["p(95)<500"],
  },
};

export default function () {
  const response = http.get(
    `${BASE_URL}/voting-sessions/${SESSION_ID}/result`,
    { tags: { endpoint: "result" } },
  );
  check(response, {
    "result was returned": (result) => result.status === 200,
  });
}
