# Script PowerShell pour ajouter des créneaux horaires via l'API
# Cela évite les erreurs SQL manuelles car le serveur gère les IDs

$baseUrl = "http://localhost:8080"
$days = @("2025-12-09", "2025-12-10", "2025-12-11", "2025-12-12", "2025-12-13")
$hours = @(
    @("08:00", "09:00"),
    @("09:00", "10:00"),
    @("10:00", "11:00"),
    @("11:00", "12:00"),
    @("14:00", "15:00"),
    @("15:00", "16:00"),
    @("16:00", "17:00")
)

foreach ($day in $days) {
    foreach ($slot in $hours) {
        $start = "$day-$($slot[0])"
        $end = "$day-$($slot[1])"
        
        $url = "$baseUrl/timeslot/$start`?endTime=$end"
        
        try {
            $response = Invoke-RestMethod -Uri $url -Method Post -ErrorAction Stop
            Write-Host "Ajouté: $day $($slot[0])-$($slot[1])" -ForegroundColor Green
        } catch {
            Write-Host "Erreur pour $day $($slot[0])-$($slot[1]): $($_.Exception.Message)" -ForegroundColor Red
        }
    }
}

Write-Host "Terminé!"
