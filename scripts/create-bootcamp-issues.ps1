# Crea las issues del bootcamp (fases 1 y 2) y el tablero kanban "BandHub Bootcamp" en GitHub.
#
# Requisitos (una sola vez):
#   winget install --id GitHub.cli
#   gh auth login
#   gh auth refresh -s project
#
# Uso, desde la raíz del repo:
#   powershell -ExecutionPolicy Bypass -File scripts/create-bootcamp-issues.ps1

$ErrorActionPreference = "Stop"
$owner = "1slander"
$repo  = "1slander/BandHub_BE"
$issues = Get-Content -Raw -Encoding UTF8 "docs/bootcamp/issues.json" | ConvertFrom-Json

gh label create bootcamp --repo $repo --color "FFE599" --description "Ticket del bootcamp" --force
gh label create fase-1   --repo $repo --color "C5DEF5" --description "Bootcamp fase 1" --force
gh label create fase-2   --repo $repo --color "BFD4F2" --description "Bootcamp fase 2" --force

$projectNumber = gh project create --owner $owner --title "BandHub Bootcamp" --format json --jq ".number"
if (-not $projectNumber) { throw "No se pudo crear el proyecto. ¿Hiciste 'gh auth refresh -s project'?" }
Write-Host "Tablero creado: proyecto #$projectNumber"

foreach ($i in $issues) {
    $tmp = [System.IO.Path]::GetTempFileName()
    # UTF-8 sin BOM para que GitHub no muestre caracteres raros
    [System.IO.File]::WriteAllText($tmp, $i.body)
    $label = if ($i.phase -eq "f1") { "fase-1" } else { "fase-2" }
    $url = gh issue create --repo $repo --title $i.title --body-file $tmp --label "bootcamp,$label"
    gh project item-add $projectNumber --owner $owner --url $url | Out-Null
    Remove-Item $tmp
    Write-Host "Creada: $($i.title)"
}

gh project link $projectNumber --owner $owner --repo $repo
Write-Host ""
Write-Host "Listo. En GitHub: tu perfil > Projects > BandHub Bootcamp."
Write-Host "Cambia la vista a 'Board' y arrastra BH-01 a 'In Progress'."
