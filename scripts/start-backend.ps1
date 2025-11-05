param(
  [string]$Profile = $(if ($env:SPRING_PROFILES_ACTIVE) { $env:SPRING_PROFILES_ACTIVE } else { "dev" }),
  [string]$DbUser = $(if ($env:DB_USERNAME) { $env:DB_USERNAME } else { "root" }),
  [string]$DbPassword = $(if ($env:DB_PASSWORD) { $env:DB_PASSWORD } else { "" })
)

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = Resolve-Path (Join-Path $scriptDir "..")

Push-Location (Join-Path $projectRoot "art-design-server")
try {
  Write-Host "Starting art-design-server (profile=$Profile, db user=$DbUser)"

  $env:SPRING_PROFILES_ACTIVE = $Profile
  $env:DB_USERNAME = $DbUser
  $env:DB_PASSWORD = $DbPassword

  mvn -s maven-settings.xml spring-boot:run
}
finally {
  Pop-Location
}
