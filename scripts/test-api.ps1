Param(
  [string]$BaseUrl = 'http://localhost:8080'
)

$ErrorActionPreference = 'Stop'

function Invoke-Api {
  param(
    [string]$Method,
    [string]$Path,
    [string]$Body
  )
  try {
    if ($Method -eq 'GET') {
      $resp = Invoke-WebRequest -Uri ("$BaseUrl$Path") -Headers @{ Accept = 'application/json' }
    } else {
      $resp = Invoke-WebRequest -Uri ("$BaseUrl$Path") -Method $Method -Headers @{ 'Content-Type' = 'application/json'; Accept = 'application/json' } -Body $Body
    }
    Write-Output ("OK " + $resp.StatusCode)
    $text = [string]$resp.Content
    if ($text.Length -gt 400) { $text.Substring(0,400) } else { $text }
  }
  catch {
    $resp = $_.Exception.Response
    if ($resp) {
      $code = [int]$resp.StatusCode
      Write-Output ("ERR " + $code)
      $reader = New-Object System.IO.StreamReader($resp.GetResponseStream())
      $body = $reader.ReadToEnd()
      if ($body) {
        if ($body.Length -gt 600) { $body.Substring(0,600) } else { $body }
      }
    } else {
      Write-Output $_
    }
  }
}

Write-Output "=== GET /api/auth/captcha ==="
Invoke-Api -Method 'GET' -Path '/api/auth/captcha'

Write-Output "`n=== POST /api/auth/login ==="
$loginBody = @{ username = 'admin'; password = 'admin123' } | ConvertTo-Json -Compress
Invoke-Api -Method 'POST' -Path '/api/auth/login' -Body $loginBody

