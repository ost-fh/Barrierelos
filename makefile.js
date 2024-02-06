const { execSync } = require('child_process');
const fs = require('fs');


process.chdir(__dirname);

const envPath = `.env`;
const envContent = fs.readFileSync(envPath, 'utf8');

function executeCommand(command) {
  try {
    execSync(command, {stdio: 'inherit'});    
  } catch (error) {
    console.error(`Error executing command: ${command}`);
    process.exit(1);
  }
}

function dockerLogin() {
  const containerRegistryToken = envContent.match(/CONTAINER_REGISTRY_TOKEN=(.*)/)[1].trim();
  const loginCommand = `echo | set /p="${containerRegistryToken}" | docker login registry.gitlab.ost.ch:45023 -u doesntmatter --password-stdin`;
  executeCommand(loginCommand);
}

function dockerPull() {
  const dockerServices = envContent.match(/DOCKER_SERVICES=(.*)/)[1].trim().replace('"', '');
  const pullCommand = `docker compose -f compose.yml -f compose-dev.yml pull ${dockerServices}`;
  executeCommand(pullCommand);
}

function dockerUp() {
  const dockerServices = envContent.match(/DOCKER_SERVICES=(.*)/)[1].trim().replace('"', '');
  const upCommand = `docker compose -f compose.yml -f compose-dev.yml up -d ${dockerServices}`;
  executeCommand(upCommand);
}

function dockerDown() {
  executeCommand('docker compose -f compose.yml -f compose-dev.yml down');
}

// Run the appropriate command based on the script argument
const scriptArgument = process.argv[2];

switch (scriptArgument) {
  case 'docker-login':
    dockerLogin();
    break;
  case 'docker-pull':
    dockerPull();
    break;
  case 'docker-up':
    dockerUp();
    break;
  case 'docker-down':
    dockerDown();
    break;
  default:
    console.error('Invalid script argument. Use one of: docker-login, docker-pull, docker-up, docker-down');
    process.exit(1);
}
