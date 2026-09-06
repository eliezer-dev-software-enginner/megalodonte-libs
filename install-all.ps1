$ROOT_DIR = Split-Path -Parent $MyInvocation.MyCommand.Path

$PROJECTS = @(
    "megalodonte-base",
    "megalodonte-reactivity",
    "megalodonte-theme",
    "megalodonte-router",
    "megalodonte-components"
)

Write-Host "Instalando bibliotecas localmente..."

foreach ($project in $PROJECTS) {

    Write-Host ""
    Write-Host "=============================="
    Write-Host "Projeto: $project"
    Write-Host "=============================="

    $projectPath = Join-Path $ROOT_DIR $project

    Set-Location $projectPath

    .\gradlew.bat build publishToMavenLocal

    if ($LASTEXITCODE -ne 0) {
        Write-Host ""
        Write-Host "ERRO ao buildar $project"
        exit 1
    }
}

Write-Host ""
Write-Host "Tudo instalado no Maven local."