Add-Type -Name Window -Namespace Console -MemberDefinition '
[DllImport("Kernel32.dll")]
public static extern IntPtr GetConsoleWindow();
[DllImport("user32.dll")]
public static extern bool ShowWindow(IntPtr hWnd, Int32 nCmdShow);
'

$consolePtr = [Console.Window]::GetConsoleWindow()
[Console.Window]::ShowWindow($consolePtr, 0)

Add-Type -AssemblyName System.Windows.Forms
Add-Type -AssemblyName System.Drawing

# Define variables
$folderName = "DannyjellTranslator"
$jarFileName = "translator.jar"
$configFileName = ".env"
$shortcutName = "Translator.lnk"
$repoOwner = "Dannyjelll"
$repoName = "translator"

# Create the main form
$form = New-Object System.Windows.Forms.Form
$form.Text = 'Translator Installer'
$form.Size = New-Object System.Drawing.Size(300,250)
$form.StartPosition = 'CenterScreen'

# Create the API Key label
$label = New-Object System.Windows.Forms.Label
$label.Location = New-Object System.Drawing.Point(10,20)
$label.Size = New-Object System.Drawing.Size(280,20)
$label.Text = 'Enter your DeepL API Key:'
$form.Controls.Add($label)

# Create the API Key text box
$textBox = New-Object System.Windows.Forms.TextBox
$textBox.Location = New-Object System.Drawing.Point(10,40)
$textBox.Size = New-Object System.Drawing.Size(260,20)
$form.Controls.Add($textBox)

# Create the progress bar
$progressBar = New-Object System.Windows.Forms.ProgressBar
$progressBar.Location = New-Object System.Drawing.Point(10,70)
$progressBar.Size = New-Object System.Drawing.Size(260,20)
$progressBar.Style = "Continuous"
$form.Controls.Add($progressBar)

# Create the status label
$statusLabel = New-Object System.Windows.Forms.Label
$statusLabel.Location = New-Object System.Drawing.Point(10,100)
$statusLabel.Size = New-Object System.Drawing.Size(260,20)
$statusLabel.Text = 'Ready to install'
$form.Controls.Add($statusLabel)

# Create the install button
$button = New-Object System.Windows.Forms.Button
$button.Location = New-Object System.Drawing.Point(10,130)
$button.Size = New-Object System.Drawing.Size(75,23)
$button.Text = 'Install'
$button.Add_Click({
    $apiKey = $textBox.Text
    
    # Create folder in user directory
    $userFolder = [Environment]::GetFolderPath("UserProfile")
    $appFolder = Join-Path -Path $userFolder -ChildPath $folderName
    New-Item -ItemType Directory -Force -Path $appFolder | Out-Null

    # Function to download the latest release
    function Get-LatestRelease {
        $statusLabel.Text = "Fetching latest release..."
        $releasesUri = "https://api.github.com/repos/$repoOwner/$repoName/releases/latest"
        $releaseData = Invoke-RestMethod -Uri $releasesUri -Method Get
        $assetUrl = $releaseData.assets | Where-Object { $_.name -like "*.jar" } | Select-Object -ExpandProperty browser_download_url -First 1
        return $assetUrl
    }

    # Download the latest JAR file with progress
    $downloadUrl = Get-LatestRelease
    $jarPath = Join-Path -Path $appFolder -ChildPath $jarFileName
    $statusLabel.Text = "Downloading JAR file..."
    $webClient = New-Object System.Net.WebClient
    $webClient.DownloadFileCompleted += {
        $statusLabel.Text = "Download completed"
        $progressBar.Value = 100
    }
    $webClient.DownloadProgressChanged += {
        $progressBar.Value = $_.ProgressPercentage
    }
    $webClient.DownloadFileAsync($downloadUrl, $jarPath)

    # Wait for download to complete
    while ($webClient.IsBusy) {
        [System.Windows.Forms.Application]::DoEvents()
    }

    # Create config file with API key
    $statusLabel.Text = "Creating config file..."
    $configContent = "DEEPL_API_KEY=`"$apiKey`""
    Set-Content -Path (Join-Path -Path $appFolder -ChildPath $configFileName) -Value $configContent

    # Create desktop shortcut
    $statusLabel.Text = "Creating desktop shortcut..."
    $WshShell = New-Object -ComObject WScript.Shell
    $Shortcut = $WshShell.CreateShortcut([Environment]::GetFolderPath("Desktop") + "\$shortcutName")
    $Shortcut.TargetPath = "javaw.exe"
    $Shortcut.Arguments = "-jar `"$jarPath`""
    $Shortcut.WorkingDirectory = $appFolder
    $Shortcut.Save()

    [System.Windows.Forms.MessageBox]::Show("Installation completed successfully!", "Success")
    $form.Close()
})
$form.Controls.Add($button)

# Show the form
$form.ShowDialog()