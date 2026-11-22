Add-Type -AssemblyName System.Drawing

$sourcePath = 'C:\Users\MADHUSUDAN\.gemini\antigravity\brain\bfaeae42-f4c0-4e7d-911d-7fd3eda190b3\.user_uploaded\media_1795266148558_bd3073a0.jpg'
if (-not (Test-Path $sourcePath)) {
    Write-Error "Source file not found: $sourcePath"
    exit 1
}

$srcImg = [System.Drawing.Image]::FromFile($sourcePath)

function Resize-AndSave($img, $width, $height, $outPath) {
    $destRect = New-Object System.Drawing.Rectangle(0, 0, $width, $height)
    $destImage = New-Object System.Drawing.Bitmap($width, $height)
    $destImage.SetResolution($img.HorizontalResolution, $img.VerticalResolution)
    $g = [System.Drawing.Graphics]::FromImage($destImage)
    $g.CompositingMode = [System.Drawing.Drawing2D.CompositingMode]::SourceOver
    $g.CompositingQuality = [System.Drawing.Drawing2D.CompositingQuality]::HighQuality
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $g.DrawImage($img, $destRect, 0, 0, $img.Width, $img.Height, [System.Drawing.GraphicsUnit]::Pixel)
    $g.Dispose()

    $parentDir = [System.IO.Path]::GetDirectoryName($outPath)
    if (-not (Test-Path $parentDir)) {
        New-Item -ItemType Directory -Path $parentDir -Force | Out-Null
    }
    $destImage.Save($outPath, [System.Drawing.Imaging.ImageFormat]::Png)
    $destImage.Dispose()
    Write-Host "Created: $outPath ($width by $height)"
}

# 1. Main Logo in drawable
Resize-AndSave $srcImg 512 512 'app\src\main\res\drawable\wealthflow_logo.png'

# 2. Mipmap launcher icons
Resize-AndSave $srcImg 48 48 'app\src\main\res\mipmap-mdpi\ic_launcher.png'
Resize-AndSave $srcImg 48 48 'app\src\main\res\mipmap-mdpi\ic_launcher_round.png'

Resize-AndSave $srcImg 72 72 'app\src\main\res\mipmap-hdpi\ic_launcher.png'
Resize-AndSave $srcImg 72 72 'app\src\main\res\mipmap-hdpi\ic_launcher_round.png'

Resize-AndSave $srcImg 96 96 'app\src\main\res\mipmap-xhdpi\ic_launcher.png'
Resize-AndSave $srcImg 96 96 'app\src\main\res\mipmap-xhdpi\ic_launcher_round.png'

Resize-AndSave $srcImg 144 144 'app\src\main\res\mipmap-xxhdpi\ic_launcher.png'
Resize-AndSave $srcImg 144 144 'app\src\main\res\mipmap-xxhdpi\ic_launcher_round.png'

Resize-AndSave $srcImg 192 192 'app\src\main\res\mipmap-xxxhdpi\ic_launcher.png'
Resize-AndSave $srcImg 192 192 'app\src\main\res\mipmap-xxxhdpi\ic_launcher_round.png'

$srcImg.Dispose()
Write-Host "Logo and icons generated successfully."
