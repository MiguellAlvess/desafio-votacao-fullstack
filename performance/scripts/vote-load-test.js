import http from "k6/http";
import exec from "k6/execution";
import { check } from "k6";

const BASE_URL = __ENV.BASE_URL || "http://localhost:8080/api/v1";
const TOTAL_VOTES = Number.parseInt(__ENV.TOTAL_VOTES || "1000", 10);
const VUS = Number.parseInt(__ENV.VUS || "100", 10);

if (!Number.isInteger(TOTAL_VOTES) || TOTAL_VOTES < 1) {
  throw new Error("TOTAL_VOTES must be a positive integer");
}

if (!Number.isInteger(VUS) || VUS < 1) {
  throw new Error("VUS must be a positive integer");
}

export const options = {
  scenarios: {
    votes: {
      executor: "shared-iterations",
      vus: VUS,
      iterations: TOTAL_VOTES,
      maxDuration: "30m",
    },
  },
  thresholds: {
    checks: ["rate>0.99"],
    "http_req_failed{endpoint:vote}": ["rate<0.01"],
    "http_req_duration{endpoint:vote}": ["p(95)<1000"],
  },
};

const jsonHeaders = {
  headers: {
    "Content-Type": "application/json",
  },
};

const calculateCpfDigit = (digits) => {
  let sum = 0;
  let weight = digits.length + 1;
  for (const digit of digits) {
    sum += Number(digit) * weight;
    weight -= 1;
  }
  const remainder = sum % 11;
  return remainder < 2 ? 0 : 11 - remainder;
};

const generateCpf = (index) => {
  const baseNumber = 100000000 + index;
  if (baseNumber > 999999999) {
    throw new Error("TOTAL_VOTES exceeds the supported CPF range");
  }
  const base = String(baseNumber);
  const firstDigit = calculateCpfDigit(base);
  const secondDigit = calculateCpfDigit(`${base}${firstDigit}`);
  return `${base}${firstDigit}${secondDigit}`;
};

const parseJson = (response, operation) => {
  if (response.status < 200 || response.status >= 300) {
    throw new Error(
      `${operation} failed with status ${response.status}: ${response.body}`,
    );
  }
  return response.json();
};

export const setup = () => {
  const associateIds = [];

  for (let index = 0; index < TOTAL_VOTES; index += 1) {
    const response = http.post(
      `${BASE_URL}/associates/identify`,
      JSON.stringify({ cpf: generateCpf(index) }),
      { ...jsonHeaders, tags: { endpoint: "associate-setup" } },
    );
    associateIds.push(parseJson(response, "Associate identification").id);
  }

  const timestamp = Date.now();
  const proposalResponse = http.post(
    `${BASE_URL}/proposals`,
    JSON.stringify({
      title: `Performance test ${timestamp}`,
      description: "Proposal created by the k6 voting load test",
    }),
    { ...jsonHeaders, tags: { endpoint: "proposal-setup" } },
  );
  const proposalId = parseJson(proposalResponse, "Proposal creation").id;

  const sessionResponse = http.post(
    `${BASE_URL}/proposals/${proposalId}/sessions`,
    JSON.stringify({ durationInMinutes: 30 }),
    { ...jsonHeaders, tags: { endpoint: "session-setup" } },
  );
  const votingSessionId = parseJson(
    sessionResponse,
    "Voting session creation",
  ).id;
  console.log(
    `Voting session created for result validation: ${votingSessionId}`,
  );
  return { associateIds, votingSessionId };
};

export const vote = (data) => {
  const iteration = exec.scenario.iterationInTest;
  const response = http.post(
    `${BASE_URL}/voting-sessions/${data.votingSessionId}/votes`,
    JSON.stringify({
      associateId: data.associateIds[iteration],
      choice: iteration % 2 === 0 ? "YES" : "NO",
    }),
    { ...jsonHeaders, tags: { endpoint: "vote" } },
  );
  check(response, {
    "vote was registered": (result) => result.status === 201,
  });
};
